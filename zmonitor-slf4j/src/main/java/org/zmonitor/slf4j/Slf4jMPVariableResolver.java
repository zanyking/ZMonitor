/**
 * 
 */
package org.zmonitor.slf4j;

import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.impl.AttributeResolveException;
import org.zmonitor.impl.DefaultMPVariableResolver;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.GetterInvocationCache;
import org.zmonitor.util.GetterInvocationCache.Getter;
import org.zmonitor.util.GetterInvocationCache.Result;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Slf4jMPVariableResolver extends DefaultMPVariableResolver {

	
	/**
	 * 
	 * @param mp
	 */
	public Slf4jMPVariableResolver(MonitorPoint mp) {
		super(mp);
	}
	
	protected Result recursive(Object target, String[] attrs, int idx){
		
		
		if(target instanceof Message){
			Object[] arr = ((Message) target).toArray();
			if(arr.length>0){
				try{
					Integer.parseInt(attrs[idx]);
					return recursiveArray(arr, attrs, idx);
				}catch(NumberFormatException e){
					//do nothing...
				}	
			}
			return recursiveBean(target, attrs, idx);
		}
		if(target instanceof Map){
			return recursiveMap((Map)target, attrs, idx);
			
		}else if(target.getClass().isArray()){
			return recursiveArray((Object[])target, attrs, idx);
			
		}else{
			return recursiveBean(target, attrs, idx);
		}
	}
}


