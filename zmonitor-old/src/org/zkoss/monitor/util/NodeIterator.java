/**
 * 
 */
package org.zkoss.monitor.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <T>
 */
public abstract class NodeIterator<T> {
	
	/**
	 * 
	 * @param nodeList
	 */
	public void iterate(NodeList nodeList){
		iterate(nodeList, null);
	}
	
	/**
	 * 
	 * @param nodeList
	 * @param arg the context object that you want to pass to {@link #forEach(int, Node, Object)}, may pass null.
	 */
	public void iterate(NodeList nodeList, T arg){
		for(int i=0, j=nodeList.getLength();i<j;i++){
			forEach(i, nodeList.item(i), arg);
		}
	}
	
	/**
	 * 
	 * @param index
	 * @param node
	 * @param arg
	 */
	protected abstract void forEach(int index, Node node, T arg);
}
