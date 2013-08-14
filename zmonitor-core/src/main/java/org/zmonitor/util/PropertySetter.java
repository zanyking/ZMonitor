/**PropertySetter.java
 * 2011/4/5
 * 
 */
package org.zmonitor.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;




import org.zmonitor.impl.ZMLog;

/**
 * @author Ian YT Tsai(Zanyking)
 * 
 */

public class PropertySetter {
	protected final Object obj;
	@SuppressWarnings("rawtypes")	
	private Map propDescs;
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public interface Interceptor {
		Object intercept(String name, String value) 
				throws PropertySetterException;
	}
	private Interceptor interceptor;
	/**
	 * 
	 * @param obj
	 */
	public PropertySetter(Object obj) {
		this(obj, null);
	}
	/**
	 * 
	 * @param obj
	 * @param interceptor
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PropertySetter(Object obj, Interceptor interceptor) {
		if(obj==null)
			throw new IllegalArgumentException("obj cannot be null");
		this.interceptor = interceptor;
		this.obj = obj;
		if(obj instanceof Map){// a map's property is its entry, no need to perform introspection. 
			propDescs = (Map) obj;
			return;
		}
		this.propDescs = new HashMap<String, PropertyDescriptor>();
		try {
			BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
			for(PropertyDescriptor propDesc : bi.getPropertyDescriptors()){
				propDescs.put(propDesc.getName(), propDesc);
			}
		} catch (IntrospectionException ex) {
			throw new PropertySetterException(ex);
		}
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setProperty(String name, String valueStr) {
		if (valueStr == null) return;
		Object val = valueStr;
		if(interceptor!=null) 
			val = interceptor.intercept(name, valueStr);
		
		if(obj instanceof Map){
			propDescs.put(name, val);
			return;
		}
		
		name = Introspector.decapitalize(name);
		PropertyDescriptor propDesc = (PropertyDescriptor) propDescs.get(name);
		if(propDesc==null){
			ZMLog.warn(" no such property \"",name,"\" in class:"+obj.getClass());
		}else{
			try{
				setProperty(propDesc, name, val, obj, valueStr!=val);		
			}catch(PropertySetterException ex){
				ZMLog.warn(ex, "Failed to load property["+name+"] " +
						"to value \""+valueStr+"\" of class:"+obj.getClass());
			}	
		}
	}
	
	private static void setProperty(PropertyDescriptor propDesc, 
			String name, Object value, Object obj, boolean transformed) {
		Method setter = propDesc.getWriteMethod();
		if(setter==null){
			throw new PropertySetterException("There's no setter method instance for property: "+name);
		}
		Class<?>[] paramTypes = setter.getParameterTypes();
		if(paramTypes.length!=1){
			throw new PropertySetterException("multiple params for setter "+name);
		}
		if(!transformed){
			try{
				value = convert((String)value, paramTypes[0]);
			}catch(Throwable t){
				throw new PropertySetterException("ConvertType Failed on property["+name+"] ");
			}	
		}
		
		try {
			setter.invoke(obj, value);
		}catch (Throwable e) {
			throw new PropertySetterException("Failed while setter method invocation", e);
		}
	}
	private static Object convert(String value, Class<?> type) {
		String val = value.trim();
		String v = val.trim();
		if (String.class.isAssignableFrom(type)) {
			return val;
		} else if (Integer.TYPE.isAssignableFrom(type)) {
			return new Integer(v);
		} else if (Long.TYPE.isAssignableFrom(type)) {
			return new Long(v);
		} else if (Boolean.TYPE.isAssignableFrom(type)) {
			if ("true".equalsIgnoreCase(v)) {
				return Boolean.TRUE;
			} else if ("false".equalsIgnoreCase(v)) {
				return Boolean.FALSE;
			}
		}
		throw new PropertySetterException(
				"Cannot find good type to convert due to type: "+type+" value:"+value);
	}

}
