/**
 * 
 */
package org.zmonitor.impl;

import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.GetterInvocationCache;
import org.zmonitor.util.GetterInvocationCache.Getter;
import org.zmonitor.util.GetterInvocationCache.Result;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultMPVariableResolver {

	protected MonitorPoint mp;
	protected static final Result NULL_OBJECT = new Result(null, null);
	
	/**
	 * 
	 * @param mp
	 */
	public DefaultMPVariableResolver(MonitorPoint mp) {
		this.mp = mp;
	}

	
	/**
	 * support <i>a.b.c</i> getter chain format.
	 * 
	 * @param attributeChain
	 *            might be: a.b.c the ZMonitor Selector Engine should be able to
	 *            handle getter chain.
	 *            
	 * @return null if any mid evaluation returned null.
	 * 
	 */
	public Object resolveVariable(String attributeChain) {
		Arguments.checkNotEmpty(attributeChain, 
				"the given attribute cannot be null!");
		
		String[] attrs = attributeChain.split("[.]");
		
		Object target = null;
		Getter getter = null;
		for(Object root :  new Object[]{mp, mp.getMonitorMeta()}){
			getter = toGetter(attrs[0], root);
			if(getter.exist()){
				target = getter.invoke(root).getValue();
				break;	
			}
		}
		if(!getter.exist()){
			throw new AttributeResolveException("neither [" +
					mp.getClass().getSimpleName()+
					"[ nor ["+mp.getMonitorMeta().getClass().getSimpleName()+
					"] can provide proper getter for attribute: "+attrs[0]);
		}
		
		if(attrs.length==1 || target==null){
			return target;
		}
		return recursive(target, attrs, 1).getValue();
	}
	
	@SuppressWarnings("rawtypes")
	protected Result recursive(Object target, String[] attrs, int idx){
		Arguments.checkNotNull(target);
		if(target instanceof Map){
			return recursiveMap((Map)target, attrs, idx);
			
		}else if(target.getClass().isArray()){
			return recursiveArray((Object[])target, attrs, idx);
			
		}else{
			return recursiveBean(target, attrs, idx);
		}
	}
	/**
	 * to support:<br>
	 * <i> .BusinessObject[logLevel=ERROR, message.abc*='hello world!']</i>
	 * <i> .BusinessObject[message.def*=123]</i>
	 * <i> .BusinessObject[message.ghi*=true]</i>
	 * 
	 * @param target
	 * @param attrs
	 * @param idx
	 * @return
	 */
	protected Result recursiveBean(Object target, String[] attrs, int idx){
		Getter getter = toGetter(attrs[idx], target);
		
		if(!getter.exist()){
			return new Result(null, new AttributeResolveException(Strings.append(
					"not able to find getter according to attribute name:\"",
					toString(attrs, idx),
					"\" getter:", getter)));// no possible getter for this attribute of current target.
		}
			
		
		if(!getter.isAbleToUse()){
			throw new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild due to security isssue"),
					getter.getError());
		}
		
		Result result = getter.invoke(target);
		Object value = result.getValue();
		if((attrs.length-idx)<=1 ){
			return result;
		}else if(value!=null){
			return recursive(value, attrs, idx + 1);
		}else if(result.hasError()){
			throw new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild due to invocation error, message:",
					result.getError().getMessage()),
						result.getError());
		}else{// mid-getter returned null, let caller decide what to do.
			return new Result(null, new AttributeResolveException(Strings.append(
					"the getter chain evaluation is faild due to bean:\"",
					toString(attrs, idx),
					"\" returned null, getter detail:", getter)));
		}
	}
	
	/**
	 * to support:<br>
	 * <i> .BusinessObject[message.abc*='hello world!']</i>
	 * <i> .BusinessObject[message.def*=123]</i>
	 * <i> .BusinessObject[message.ghi*=true]</i>
	 * 
	 * @param map
	 * @param attrs
	 * @param idx
	 * @return
	 */
	protected Result recursiveMap(@SuppressWarnings("rawtypes") Map map, String[] attrs, int idx ){
		Object value = map.get(attrs[idx]);
		
		if((attrs.length-idx)<=1 ){//reach the end
			return (value==null) ?
					NULL_OBJECT : new Result(value, null);
		}else if(value!=null){
			return recursive(value, attrs, idx + 1);
		}else{// hasn't reach the end, but is not able to continue due to mid-getter returned null.
			return new Result(null, new AttributeResolveException(Strings.append(
					"the getter chain evaluation is faild due to bean:\"",
					toString(attrs, idx),
					"\" returned null, getter detail:", map)));
		}
	}
	/**
	 * to support:<br>
	 * <i> .BusinessObject[message.0*='hello world!']</i>
	 * <i> .BusinessObject[message.13=123]</i>
	 * <i> .BusinessObject[message.5=true]</i>
	 * 
	 * @param list
	 * @param attrs
	 * @param idx
	 * @return
	 */
	protected Result recursiveArray(Object[] array, String[] attrs, int idx ){

		if(array.length==0)return NULL_OBJECT;
		int listIdx = -1;
		try{
			listIdx = Integer.parseInt(attrs[idx]);
		}catch(NumberFormatException e){
			new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild, the attribute \"",attrs[idx]
					,"\" is not integer"),
						e);
		}
		Object value = null;
		if(array.length>=listIdx){
			value = array[listIdx];	
		}
		
		
		if((attrs.length-idx)<=1 ){
			return new Result(value, null);
		}else if(value!=null){
			return recursive(value, attrs, idx + 1);
		}else{
			return new Result(null, new AttributeResolveException(Strings.append(
					"the getter chain evaluation is faild due to bean:\"",
					toString(attrs, idx),
					"\" returned null, getter detail")));
		}
	}
	

	
	protected static Getter toGetter(String attribute, Object target){
		return GetterInvocationCache.SINGELTON.get(
				target.getClass(), attribute);
	}
	
	protected static String toString(String[] arr, int idx){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<idx;i++){
			sb.append(arr[i]);
		}
		return sb.toString();
	}
}


