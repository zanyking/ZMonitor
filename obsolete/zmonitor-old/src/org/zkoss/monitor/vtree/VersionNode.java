/**MergPoint.java
 * 2011/3/6
 * 
 */
package org.zkoss.monitor.vtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.monitor.spi.Name;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class VersionNode extends AbstractNode{
	
	private final List<VersionNodeChildren> childrens;
	private final int stack;
	private final int index;
	private final Name name;
	private VersionNodeChildren parent;

	/**
	 * 
	 * @param parent
	 * @param stack
	 * @param index
	 * @param name
	 * @param mesg
	 * @param mergeOperator 
	 */
	public VersionNode( int stack, int index, Name name) {
		super();
		this.stack = stack;
		this.index = index;
		this.name = name;
		childrens = new LinkedList<VersionNodeChildren>();
	}
	

	
	public Name getName(){
		return name;
	}
	
	public boolean isLeaf(){
		return childrens.size()==0;
	}
	
	public List<VersionNodeChildren> getChildrens() {
		return new ArrayList<VersionNodeChildren>(childrens);
	}
	
	public void addChildren(VersionNodeChildren children) {
		children.setParent(this);
		childrens.add(children);
	}

	public int getStack() {
		return stack;
	}

	public int getIndex() {
		return index;
	}

	public VersionNodeChildren getParent() {
		return parent;
	}

	public void setParent(VersionNodeChildren parent) {
		this.parent = parent;
	}

	
}
