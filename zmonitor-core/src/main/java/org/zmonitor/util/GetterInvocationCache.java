/**
 * 
 */
package org.zmonitor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	
	private final Map<Class<?>, ConcurrentHashMap<String, Getter>> pool = 
			new HashMap<Class<?>,  ConcurrentHashMap<String, Getter>>();
	
	/**
	 * 
	 * @param attribute the attribute representation of getter. getAbc() -> "abc".
	 * @param target the instance which will be invoked .
	 * @return the value of the invocation.
	 */
	public Result invoke(String attribute, Object target){
		Arguments.checkNotNull(target);
		Getter getter = get(target.getClass(), attribute);
		return getter.invoke(target);
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public class Getter{
		private Method method = NO_SUCH_GETTER;
		private Exception error;
		private String attribute;
		
		public Getter(String attribute) {
			this.attribute = attribute;
		}

		public Method getMethod() {
			return method;
		}

		public Exception getError() {
			return error;
		}

		public boolean isAbleToUse(){
			return error==null;
		}
		public String toString(){
			if(!exist()) 
				return "["+attribute+"] NO_SUCH_GETTER";
			if(!isAbleToUse())
				return "["+attribute+"] ERROR: "+error.getMessage();
			return "["+attribute+"]" +
				method.getDeclaringClass()+"::"+method.getName();
		}
		public boolean exist(){
			return method!=NO_SUCH_GETTER;
		}
		
		public Result invoke(Object target){
			if(exist() && isAbleToUse()){
				Object value = null;
				Exception error = null;
				try {
					value = method.invoke(target);
				}  catch (IllegalAccessException e) {
					error = e;
					ZMLog.info("access not allow, class: "+target.getClass(),", method:",method,"\n", e);
				} catch (InvocationTargetException e) {
					error = e;
					ZMLog.info("InvocationTargetException, class: "+target.getClass(),", method:",method,"\n cause exception", e.getTargetException());
				}
				return new Result(value, error);
			}
			throw new IllegalStateException("the getter method is not exist or not able to use.");
		}
	}
	
	public static class Result{
		private final Object value;
		private final Exception error;
		public Result(Object value, Exception error) {
			this.value = value;
			this.error = error;
		}
		public Object getValue() {
			return value;
		}
		public Exception getError() {
			return error;
		}
		public boolean hasError(){
			return error!=null;
		}
		public boolean hasValue(){
			return value!=null;
		}
	}
	
	
	/**
	 *  never return null!
	 * @param clz
	 * @param attributeName
	 * @return if null, there's no such method.
	 */
	public Getter get(Class<?> clz, String attribute){
		Arguments.checkNotEmpty(attribute);
		// lazy sync for methods 
		ConcurrentHashMap<String, Getter> getters = pool.get(clz);
		if(getters==null){
			synchronized(pool){
				getters = pool.get(clz);
				if(getters==null){
					pool.put(clz, getters = 
						new ConcurrentHashMap<String, Getter>());
				}
			}	
		}
		// lazy sync for method
		Getter getter = getters.get(attribute);
		
		if (getter == null) {
			getter = new Getter(attribute);
			Method candidate = null;
			try {
				candidate = clz.getMethod("get"
						+ Strings.capitalize(attribute));
			} catch (NoSuchMethodException e) {
				// no such method
			} catch (SecurityException e) {
				getter.error = e;
			}
			if (candidate == null) {
				try {
					candidate = clz.getMethod("is"
							+ Strings.capitalize(attribute));
				} catch (NoSuchMethodException e) {
					// no such method
				} catch (SecurityException e) {
					getter.error = e;
				}
			}
			if(candidate!=null){
				getter.method = candidate;
			}
			getters.putIfAbsent(attribute, getter);
		}
		return getter;
	}
	
	
	

	
	private static final Method NO_SUCH_GETTER ;
	static {
		try {
			NO_SUCH_GETTER = NullMethod.class.getMethod("noSuchGetter");
		} catch (Throwable e) {//won't happen, just to avoid the checked Exception.
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}

class NullMethod{
	public void noSuchGetter(){}
}
  
