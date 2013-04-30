/**
 * 
 */
package org.zmonitor.impl;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.DOMs;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class XMLConfigurationImpl implements XMLConfiguration{
	protected final DOMRetriever xmlDoc;
	/**
	 * 
	 * @param xmlDoc
	 */
	public XMLConfigurationImpl(DOMRetriever xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	/**
	 * 
	 * @param xmlTextContent
	 */
	public XMLConfigurationImpl(String xmlTextContent){
		this.xmlDoc = new DOMRetriever(xmlTextContent);
	}
	
	
	public Node getNode(String xPath, Node parentNode){
		
		NodeList nList = (parentNode==null) ?
				xmlDoc.getNodeList(xPath) : 
				xmlDoc.getNodeList(parentNode, xPath);
				
		return nList.item(0);
	}
	
	public void forEach(Node root, String xPath, Visitor visitor) {
		NodeList nodeList = xmlDoc.getNodeList(root, xPath);
		for(int i=0, j=nodeList.getLength();i<j;i++){
			boolean b = visitor.visit(i, nodeList.item(i));
			if(!b)break;
		}
	}
	
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String PROPERTY = "property";
	/**
	 * 
	 * @param xmlDoc
	 * @param node
	 * @param setter
	 */
	public void applyPropertyTagsToBean(Node node, final PropertySetter setter){
		
		forEach(node, PROPERTY, new Visitor(){
			public boolean visit(int idx, Node propNode) {
				String name = DOMs.getAttributeValue(propNode, NAME);
				String value = XMLConfigs.getTextFromAttrOrContent(propNode, VALUE);
				setter.setProperty(name, value);
				return true;
			}});
	}

	/**
	 * 
	 * @return
	 */
	public DOMRetriever getDomRetriever() {
		return xmlDoc;
	}
	
	

	
}
