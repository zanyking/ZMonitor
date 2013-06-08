/**
 * 
 */
package org.zmonitor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.zmonitor.impl.ZMLog;

/**
 * Is designed for ZMonitor internal use only, please do not use this if you are not extending zmonitor functionality.
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class GetterInvocationCache {
	private GetterInvocationCache() {}
	
	public static final GetterInvocationCache SINGELTON = new GetterInvocationCache();
	
	
	private final Map<Class<?>, Map<String, Method>> pool = 
			new HashMap<Class<?>,  Map<String, Method>>();
	
	/**
	 * 
	 * @param attribute the attribute representation of getter. getAbc() -> "abc".
	 * @param target the instance which will be invoked .
	 * @return the value of the invocation.
	 */
	public <T> T invoke(String attribute, Object target){
		Method method = get(target.getClass(), attribute);
		
		if(method==null) return null;		

		try {
			return (T) method.invoke(target);
		}  catch (IllegalAccessException e) {
			ZMLog.info("access not allow, class: "+target.getClass(),", method:",method,"\n", e);
		} catch (InvocationTargetException e) {
			ZMLog.info("InvocationTargetException, class: "+target.getClass(),", method:",method,"\n cause exception", e.getTargetException());
		}
		return null;	
	}
	/**
	 *  
	 * @param clz
	 * @param attributeName
	 * @return if null, there's no such method.
	 */
	public Method get(Class<?> clz, String attributeName){
		// lazy sync for methods 
		Map<String, Method> methods = pool.get(clz);
		if(methods==null){
			synchronized(pool){
				methods = pool.get(clz);
				if(methods==null){
					pool.put(clz, methods = new HashMap<String, Method>());
				}
			}	
		}
		// lazy sync for method
		Method method = methods.get(attributeName);;
		if(method==null){
			synchronized(methods){
				method = methods.get(attributeName);
				if(method==null){
					try {
						method = clz.getMethod(
								"get"+Strings.capitalize(attributeName));
					} catch (NoSuchMethodException e) {
						// no such method
					} catch (SecurityException e) {
						// SecurityManager doesn't like you
					} 
					if(method==null){
						try {
							method = clz.getMethod(
									"is"+Strings.capitalize(attributeName));
						} catch (NoSuchMethodException e) {
							// no such method
						} catch (SecurityException e) {
							// SecurityManager doesn't like you
						}
					}
				}
			}
		}
		return method;
	}
}
