/**VersionChildren.java
 * 2011/3/9
 * 
 */
package org.zkoss.monitor.vtree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class VersionNodeChildren extends AbstractNode{
	private List<VersionNode> nodes = new ArrayList<VersionNode>();
	private VersionNode parent;
	
	public List<VersionNode> getAll() {
		return nodes;
	}
	
	public void add(VersionNode vNode){
		vNode.setParent(this);
		nodes.add(vNode);
	}
	
	public int size(){
		return nodes.size();
	}

	public VersionNode getParent() {
		return parent;
	}
	public void setParent(VersionNode parent) {
		this.parent = parent;
	}
	
}
