/**
 * 
 */
package org.zmonitor.impl;

import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.DOMs;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfigContextImpl implements ConfigContext{

	private final Map<String, String> attrs;
	private final ZMonitorManager zMonitorManager;
	private final DOMRetriever domRetriever;
	private final Node currentNode;
	
	/**
	 * 
	 * @param attrs
	 * @param zMonitorManager
	 * @param domRetriever
	 * @param currentNode
	 */
	public ConfigContextImpl(Map<String, String> attrs,
			ZMonitorManager zMonitorManager, 
			DOMRetriever domRetriever, 
			Node currentNode) {
		
		this.attrs = attrs;
		this.zMonitorManager = zMonitorManager;
		this.domRetriever = domRetriever;
		this.currentNode = currentNode;
	}
	
	public String get(String key){
		return attrs.get(key);
	}
	
	public ZMonitorManager getManager() {
		return zMonitorManager;
	}
	
	public ConfigContext toNode(String xPath) {
		NodeList nList = (currentNode==null) ?
				domRetriever.getNodeList(xPath) : 
					domRetriever.getNodeList(currentNode, xPath);
		Node next = nList.item(0);
		
		return new ConfigContextImpl(
			attrs, zMonitorManager, domRetriever, next);
	}

	public void forEach(String xPath, Visitor visitor) {
		NodeList nodeList = domRetriever.getNodeList(currentNode, xPath);
		for(int i=0, j=nodeList.getLength();i<j;i++){
			boolean b = visitor.visit(i, nodeList.item(i));
			if(!b)break;
		}
	}

	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String PROPERTY = "property";
	
	public void applyPropertyTags(final PropertySetter setter) {
		forEach( PROPERTY, new Visitor(){
			public boolean visit(int idx, Node propNode) {
				String name = DOMs.getAttributeValue(propNode, NAME);
				String value = XMLConfigs.getTextFromAttrOrContent(propNode, VALUE);
				setter.setProperty(name, value);
				return true;
			}});
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
