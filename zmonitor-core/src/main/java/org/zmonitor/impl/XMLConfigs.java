/**XMLConfigs.java
 * 2011/4/6
 * 
 */
package org.zmonitor.impl;

import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Node;
import org.zmonitor.WrongConfigurationException;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.NodeIterator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class XMLConfigs {
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String PROPERTY = "property";
	
	private static final HashSet<String> ignoreClass =  new HashSet<String>();
	static{
		ignoreClass.add("class");
	}
	/**
	 * 
	 * @param strs
	 * @return
	 */
	public static Set<String> ignores(String... strs){
		if(strs.length==1 && "class".equals(strs[0])){
			return ignoreClass;// using this very often
		}
		HashSet<String> ignores = new HashSet<String>();
		for(String str : strs){
			ignores.add(str);
		}
		return ignores;
	}
	
	/**
	 * 
	 * @param xmlDoc
	 * @param node
	 * @param setter
	 */
	public static void applyPropertyTagsToBean(DOMRetriever xmlDoc, 
			Node node, PropertySetter setter){
		new NodeIterator<PropertySetter>() {
			protected void forEach(int index, Node propNode, PropertySetter setter) {
				String name = DOMRetriever.getAttributeValue(propNode, NAME);
				String value = getTextFromAttrOrContent(propNode, VALUE);
				setter.setProperty(name, value);
			}
		}.iterate(xmlDoc.getNodeList(node, PROPERTY), setter);
	}
	/**
	 * 
	 * @param xmlDoc
	 * @param node
	 * @param setter
	 * @param ignore
	 */
	public static void applyAttributesToBean(DOMRetriever xmlDoc, 
			Node node, PropertySetter setter, Set<String> ignore){
		Properties attrs = DOMRetriever.getAttributes(node);
		for(Entry<Object, Object> attr : attrs.entrySet()){
			String name = (String)attr.getKey();
			if(ignore!=null && ignore.contains(name)){
				continue;
			}
			setter.setProperty(name, (String)attr.getValue());
		}
	}
	public static String getTextFromAttrOrContent(Node node, String attribute){
		String value = DOMRetriever.getAttributeValue(node, attribute);
		if(Strings.isEmpty(value)){
			value = DOMRetriever.getTextValue(node);
		}
		return value;
	}

	/**
	 * 
	 * @param <T>
	 * @param node
	 * @param defaultClass
	 * @param mustHave
	 * @return
	 */
	public static <T> T newInstanceByClassAttr(Node node, Class<T> defaultClass, boolean mustHave){
		String clazzStr = DOMRetriever.getAttributeValue(node, "class");

		if(Strings.isEmpty(clazzStr) && defaultClass==null && mustHave)
			throw new WrongConfigurationException(Strings.append(
					"\"class\" of this Node:",node," cannot be null or empty! classStr:",clazzStr));
		
		return newInstance(clazzStr, defaultClass);
	}
	/**
	 * 
	 * @param <T>
	 * @param clazzStr
	 * @param defaultClass
	 * @return
	 */
	public static <T> T newInstance(String clazzStr, Class<T> defaultClass){
		Class<T> clazz = null;
		try {
			if(Strings.isEmpty(clazzStr)){
				clazz = defaultClass;
			}else {
				clazz = (Class<T>) Class.forName(clazzStr);
			}
			if(clazz==null)
				throw new WrongConfigurationException("Both class String and defaultClass are NULL!");
			return (T) clazz.newInstance();
			
		} catch (ClassNotFoundException e) {
			throw new WrongConfigurationException(e);
		}catch (InstantiationException e) {
			throw new WrongConfigurationException(Strings.append(
					"class [",clazz,"] must has default Constructor! "), e);
		} catch (IllegalAccessException e) {
			throw new WrongConfigurationException(Strings.append(
					"class [",clazz,"]'s default Constructor must be public! "), e);
		} 
	}

}
