/**
 * 
 */
package org.zmonitor.impl;

import java.util.Map;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.spi.XMLConfiguration.Visitor;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfigContextImpl implements ConfigContext{

	private final Map<String, String> attrs;
	private final ZMonitorManager zMonitorManager;
	private final XMLConfiguration configuration;
	private final Node currentNode;
	
	/**
	 * 
	 * @param jarContext
	 * @param zMonitorManager
	 * @param configuration
	 */
	public ConfigContextImpl(Map<String, String> attrs,
			ZMonitorManager zMonitorManager, 
			XMLConfiguration configuration) {
		this(attrs, zMonitorManager, configuration, null);
	}
	/**
	 * 
	 * @param attrs
	 * @param zMonitorManager
	 * @param configuration
	 * @param currentNode
	 */
	public ConfigContextImpl(Map<String, String> attrs,
			ZMonitorManager zMonitorManager, 
			XMLConfiguration configuration, 
			Node currentNode) {
		
		this.attrs = attrs;
		this.zMonitorManager = zMonitorManager;
		this.configuration = configuration;
		this.currentNode = currentNode;
	}
	
	public String get(String key){
		return attrs.get(key);
	}
	
	public ZMonitorManager getManager() {
		return zMonitorManager;
	}
	
	public XMLConfiguration getConfiguration() {
		return configuration;
	}
	
	public ConfigContext query(String xPath) {
		Node next = configuration.getNode(xPath, null);
		
		return new ConfigContextImpl(
			attrs, zMonitorManager, configuration, next);
	}

	public void forEach(String xPath, Visitor visitor) {
		configuration.forEach(currentNode, xPath, visitor);
	}

	public void applyPropertyTags(PropertySetter setter) {
		configuration.applyPropertyTagsToBean(currentNode, setter);
	}
	public void applyAttributes(PropertySetter setter, String... ignores) {
		XMLConfigs.applyAttributesToBean(
			currentNode, setter, XMLConfigs.ignores(ignores));
	}
	public Node getNode() {
		return currentNode;
	}
	public <T> T newBean(Class<T> defaultClass, boolean mustHave) {
		return XMLConfigs.newInstanceByClassAttr(currentNode, defaultClass, mustHave);
	}



}
