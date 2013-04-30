/**
 * 
 */
package org.zmonitor.spi;

import org.w3c.dom.Node;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.PropertySetter;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface XMLConfiguration {
	/**
	 * 
	 * @param xPath
	 * @param parentNode
	 * @return the first node of the given xpath
	 */
	public Node getNode(String xPath, Node parentNode);
	
	/**
	 * 
	 * @param root
	 * @param xPath
	 * @param visitor
	 */
	public void forEach(Node root, String xPath, Visitor visitor);

	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 * @param <T>
	 */
	interface Visitor{
		/**
		 * 
		 * @param idx
		 * @param node
		 * @param self
		 * @return true if you want to continue, false break the iteration.
		 */
		public boolean visit(int idx, Node node);
	}
	/**
	 * 
	 * @param node
	 * @param setter
	 */
	public void applyPropertyTagsToBean(Node node, final PropertySetter setter);
}
