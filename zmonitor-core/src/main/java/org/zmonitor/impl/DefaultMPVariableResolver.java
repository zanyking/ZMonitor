/**
 * 
 */
package org.zmonitor.impl;

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
	
	/**
	 * 
	 * @param mp
	 */
	public DefaultMPVariableResolver(MonitorPoint mp) {
		this.mp = mp;
	}

	
	/**
	 * 
	 * @param attributeChain
	 *            might be: a.b.c the ZMonitor Selector Engine should be able to
	 *            handle getter chain.
	 * @return
	 */
	public Object resolveVariable(String attributeChain) {
		String[] attrs = attributeChain.split("[.]");
		
		Object[] roots = new Object[]{mp, mp.getMonitorMeta()};
		Object result = null;
		for(Object root : roots){
			result = recusiveGet(root, attrs, 0);
		}
		return result;
		
	}
	
	private static Result recusiveGet(Object target, String[] attrs, int idx){
		Arguments.checkNotNull(target);
		Getter getter = getter1(attrs[idx], target);
		
		if(!getter.exist())
			return null;
		if(!getter.isAbleToUse()){
			throw new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild due to security isssue"),
					getter.getError());
		}
		
		Result result = getter.invoke(target);
		
		if((attrs.length-idx)<=1 ){
			return result;
		}else if(result.getValue()!=null){
			return recusiveGet(result.getValue(), attrs, idx + 1);
		}else if(result.hasError()){
			throw new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild due to invocation error, message:",
					result.getError().getMessage()),
						result.getError());
		}else{
			throw new AttributeResolveException(Strings.append(
					"the getter chain evaluation: \"",
					toString(attrs, idx),
					"\" is faild due to getter returned null: ", getter));
		}
	}

	
	private static Getter getter1(String attribute, Object target){
		return GetterInvocationCache.SINGELTON.get(
				target.getClass(), attribute);
	}
	
	private static String toString(String[] arr, int idx){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<idx;i++){
			sb.append(arr[i]);
		}
		return sb.toString();
	}
}


