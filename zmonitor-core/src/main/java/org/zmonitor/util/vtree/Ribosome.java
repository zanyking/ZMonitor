/**MergeOperator.java
 * 2011/3/9
 * 
 */
package org.zmonitor.util.vtree;

import java.util.List;
import java.util.Stack;

import org.zmonitor.MonitorPoint;
import static org.zmonitor.util.MPTrees.*;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Ribosome {
	Ribosome(){}
	/**
	 * @author Ian YT Tsai
	 */
	private class WriteNodeCtx{
		final MonitorPoint record; 
		final VersionNode vNode;
		/**
		 * 
		 * @param rec
		 * @param target
		 */
		public WriteNodeCtx(MonitorPoint rec, VersionNode target) {
			if(rec==null || target==null)
				throw new IllegalArgumentException("can't be null! rec:"+rec+",  target:"+target);
			this.record = rec;
			this.vNode = target;
		}
	}//end of class...
	
	/**
	 * 
	 * @param rootRec
	 * @param vTree
	 */
	/*PACKAGE*/void translate( MonitorPoint rootRec, VersionTree vTree){
		Stack<WriteNodeCtx> vNodeStack = new Stack<WriteNodeCtx>();
		VersionNode rootVNode = vTree.getRootVNode();
		writeRec2Measurable(rootRec, rootVNode);
		VersionNode reqVNode = initRequestVNode(rootRec, rootVNode);
		
		vNodeStack.add(new WriteNodeCtx(rootRec, reqVNode));
		WriteNodeCtx wnCtx ;
		while(!vNodeStack.isEmpty()){
			wnCtx = vNodeStack.pop();
			write(wnCtx.record, wnCtx.vNode, vNodeStack);
		}
	}

	private VersionNode initRequestVNode(MonitorPoint root, VersionNode rootVNode){
		VersionNode reqVNode = null;
		for(VersionNodeChildren cildren : rootVNode.getChildrens()){
			reqVNode = cildren.getAll().get(0);
			// identification
			if(isSimilar(reqVNode, root) ){
				writeRec2Measurable(root, cildren);
				return reqVNode;
			}
		}
		VersionNodeChildren vChildren = new VersionNodeChildren();
		rootVNode.addChildren(vChildren);
		writeRec2Measurable(root, vChildren);
		vChildren.add(reqVNode = new VersionNode(0, 0, root.getCallerInfo()));
		return reqVNode;
	}
	
	
	private void write( MonitorPoint record, VersionNode target, final Stack<WriteNodeCtx> vNodeStack){
		// rewrite this vnode it's self first
		writeRec2Measurable(record, target);

		//iterate every version of children, see if there's any matches.
		VersionNodeChildren vChildren = null;
		for(VersionNodeChildren children : target.getChildrens()){
			boolean isSame = match(record.getChildren(), children.getAll());
			if(isSame){
				vChildren = children;
				break;
			}
		}
		
		if(vChildren==null){//TODO: create new children for this record
			int idx = 0;
			if(!record.isLeaf()){
				target.addChildren(vChildren = new VersionNodeChildren());	
				writeRec2Measurable(record, vChildren);
				VersionNode newVNode;
				for(MonitorPoint rec : record.getChildren()){
					newVNode = new VersionNode(target.getStack()+1, idx++, rec.getCallerInfo());			
					vChildren.add(newVNode);
					vNodeStack.add(new WriteNodeCtx(rec, newVNode));
				}	
			}
		}else{
			writeRec2Measurable(record, vChildren);
			zipping(record.getChildren(), vChildren.getAll(), new Operator(){
				public boolean operate(MonitorPoint record, VersionNode vNode) {
					vNodeStack.add(new WriteNodeCtx(record, vNode));
					return true;
				}});
		}
	}
	
	
	private static boolean isSimilar( VersionNode vn, MonitorPoint mp){
		throw new UnsupportedOperationException("not impl yet");//TODO not impl yet.
	}	
	
	
	// might has customization in the future.
	private void writeRec2Measurable( MonitorPoint rec, AbstractNode aNode){
		RecordSummary<Object> summary = aNode.getRecordSummary();
		
		if(summary==null){
			aNode.setRecordSummary(summary = new RecordSummary<Object>(1));
		}
		summary.append(rec.getMessage());
		summary.accumulate(retrieveMillisFromPrevious(rec), 
				retrieveMillisToNext(rec));
	}
	
	private static boolean match(List<MonitorPoint> records, List<VersionNode> children){
		// identify if these two lists are similar
		if(records.size() != children.size())return false;
		
		final boolean[] flag = new boolean[]{true};
		zipping(records, children, new Operator(){
			public boolean operate(MonitorPoint record, VersionNode vNode) {
				if(!isSimilar(vNode, record)){
					flag[0] = false;	
				}
				return flag[0];
			}});
		return flag[0];
	}
	
	private static void zipping(List<MonitorPoint> records, List<VersionNode> children, Operator op){
		if(records.size() != children.size())
			throw new IllegalArgumentException("the size of these two list must be the same!");
		MonitorPoint[] recArr = new MonitorPoint[records.size()];
		recArr = records.toArray(recArr);
		
		VersionNode[] vnArr = new VersionNode[recArr.length];
		vnArr = children.toArray(vnArr);
		
		for(int i=0;i<recArr.length;i++){
			if(!op.operate(recArr[i], vnArr[i]))return;
		}
	}
	/**
	 * 
	 * @author Ian YT Tsai
	 *
	 */
	private interface Operator{
		boolean operate(MonitorPoint record, VersionNode vNode);
	}//end of class
	

	
}
