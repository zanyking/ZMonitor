/**MergPointStore.java
 * 2011/3/6
 * 
 */
package org.zkoss.monitor.vtree;

import org.zkoss.monitor.MeasurePoint;
import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.spi.Name;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class VersionTree{
	
	boolean hasHandledBefore;
	private VersionNode rootVNode;
	private Name psendoRootName = new StringName( "Application Requests", "");
	
	
	
	public Name getPsendoRootName() {
		return psendoRootName;
	}
	
	public void setPsendoRootName(Name psendoRootName) {
		this.psendoRootName = psendoRootName;
	}
	/**
	 * 
	 * @return
	 */
	VersionNode getRootVNode() {
		if(rootVNode==null){
			rootVNode = new VersionNode( 0, 0, psendoRootName);	
		}
		return rootVNode;
	}
	/**
	 * 
	 * @param tl
	 */
	public void apply(Timeline tl) {
		MeasurePoint root = tl.getRoot();
		Ribosome ribosome = new Ribosome();
		ribosome.translate(root, this);
		hasHandledBefore = true;
	}
	/**
	 * 
	 * @param visitor
	 */
	public void accept(VTreeVisitor visitor){
		if(!hasHandledBefore)return;
		new VTreeParser().parse(getRootVNode(), visitor);
	}
	
	/**
	 * 
	 * @author Ian YT Tsai
	 */
	private static class VTreeParser {

		public boolean parse( VersionNode vNode, VTreeVisitor visitor ){
			if(vNode==null)return true;
			BoolRef returnFlag = new BoolRef(); 
			returnFlag.set(visitor.begin(vNode));
			
			if(vNode.isLeaf()){
				returnFlag.set(visitor.end(vNode));
				return returnFlag.get();
			}

			for(VersionNodeChildren children : vNode.getChildrens()){
				if(!parse( children, visitor)){
					returnFlag.set(false);
					break;
				}
			}
			returnFlag.set(visitor.end(vNode));
			return returnFlag.get();
		}
		
		private boolean parse( VersionNodeChildren children, VTreeVisitor visitor){
			BoolRef returnFlag = new BoolRef(); 
			returnFlag.set(visitor.begin(children));
			
			for(VersionNode vNode : children.getAll()){
				if(!parse( vNode, visitor)){
					returnFlag.set(false);
					break;
				}
			}
			returnFlag.set(visitor.end(children));
			return returnFlag.get();
		}
		
		private class BoolRef{
			private boolean flag = true;
			boolean set(boolean newFlag){
				if(flag){
					flag = newFlag;
				}
				return flag;
			}
			boolean get(){
				return flag;
			}
		}
	}//end of class...
}
