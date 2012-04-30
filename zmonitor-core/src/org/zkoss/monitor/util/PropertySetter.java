/**PropertySetter.java
 * 2011/4/5
 * 
 */
package org.zkoss.monitor.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.zkoss.monitor.impl.ZMLog;

/**
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class PropertySetter {
	protected final Object obj;
	private final HashMap<String, PropertyDescriptor> propDescs;
	/**
	 * 
	 * @param obj
	 */
	public PropertySetter(Object obj) {
		if(obj==null)
			throw new IllegalArgumentException("obj cannot be null");
		
		this.obj = obj;
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
	public void setProperty(String name, String value) {
		if (value == null) return;
		name = Introspector.decapitalize(name);
		PropertyDescriptor propDesc = propDescs.get(name);
		if(propDesc==null){
			ZMLog.warn(" no such property \"",name,"\" in class:"+obj.getClass());
		}else{
			try{
				setProperty(propDesc, name, value, obj);		
			}catch(PropertySetterException ex){
				ZMLog.warn(ex, "Failed to load property["+name+"] " +
						"to value \""+value+"\" of class:"+obj.getClass());
			}	
		}
	}
	
	/**
	 * 
	 * @param propDesc
	 * @param name
	 * @param value
	 */
	private static void setProperty(PropertyDescriptor propDesc, String name, String value, Object obj) {
		Method setter = propDesc.getWriteMethod();
		if(setter==null){
			throw new PropertySetterException("There's no setter method insance for property: "+name);
		}
		Class<?>[] paramTypes = setter.getParameterTypes();
		if(paramTypes.length!=1){
			throw new PropertySetterException("multiple params for setter "+name);
		}
		Object arg;
		try{
			arg = convert(value, paramTypes[0]);
		}catch(Throwable t){
			throw new PropertySetterException("ConvertType Failed on property["+name+"] ");
		}
		try {
			setter.invoke(obj, arg);
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
