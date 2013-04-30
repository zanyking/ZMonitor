/**
 * 
 */
package org.zmonitor.util;

import java.util.Properties;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DOMs {
	private DOMs(){}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public static String getTextValue(Node n) {
		if (n == null)
			return ""; //$NON-NLS-1$
		return n.getTextContent();
	}
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static String getAttributeValue(Node node, String name) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm == null)
			return null;
		Node n = nnm.getNamedItem(name);
		if (n != null)
			return n.getNodeValue();
		return ""; //$NON-NLS-1$
	}
	/**
	 * 
	 * @param node
	 * @return
	 */
	public static Properties getAttributes(Node node) {
		Properties props = new Properties();
		NamedNodeMap nnm = node.getAttributes();
		if (nnm == null)return props;
		
		for(int i=0, index = nnm.getLength(); i<index ;i++){
			Node temp = nnm.item(i);
			props.setProperty(temp.getNodeName(), temp.getNodeValue());
		}
		return props;
	}
}
