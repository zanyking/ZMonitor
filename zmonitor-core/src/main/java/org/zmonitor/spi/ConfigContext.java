/**
 * 
 */
package org.zmonitor.spi;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.impl.XmlConfiguratorLoader;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfigContext {
	protected final DOMRetriever xmlDoc;
	/**
	 * 
	 * @param xmlDoc
	 */
	public ConfigContext(DOMRetriever xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	/**
	 * 
	 * @param xmlTextContent
	 */
	public ConfigContext(String xmlTextContent){
		this.xmlDoc = new DOMRetriever(xmlTextContent);
	}
	
	/**
	 * 
	 * @param singletonXPath
	 * @param xmlDoc
	 * @param parentNode
	 * @return
	 */
	public Node getSingleton(String singletonXPath, Node parentNode){
		
		NodeList nList = xmlDoc.getNodeList(parentNode, singletonXPath);
		int length = nList.getLength();
		if(length>1){
			throw new IllegalArgumentException(Strings.append("you got multiple ",singletonXPath,
					" in your " ,XmlConfiguratorLoader.ZMONITOR_XML,
					", which one is that you want? length=",length));
		}
		return nList.item(0);
	}
	/**
	 * 
	 * @return
	 */
	public DOMRetriever getDomRetriever() {
		return xmlDoc;
	}
}
