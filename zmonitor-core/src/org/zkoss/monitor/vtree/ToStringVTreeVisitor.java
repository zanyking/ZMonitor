/**ToStringParseInterceptor.java
 * 2011/3/14
 * 
 */
package org.zkoss.monitor.vtree;

import java.util.LinkedList;

import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ToStringVTreeVisitor extends VTreeVisitor {

	private final StringBuffer sb = new StringBuffer();
	private final CurrentIndent prefix;
	
	
	public ToStringVTreeVisitor(String indent) {
		super();
		prefix = new CurrentIndent(indent);
	}
	
	@Override
	public boolean begin(VersionNode vNode) {
		printNode( vNode, prefix, " ["+vNode.getName()+"]", true);
		prefix.push();
		return true;
	}

	@Override
	public boolean end(VersionNode node) {
		prefix.pop();
		return true;
	}

	@Override
	public boolean begin(VersionNodeChildren children) {
		printNode( children, prefix, " DECISION_BRANCH", false);
		if(children.size()>1){
			Strings.appendln(sb, prefix, "{");
			prefix.push();
		}
		return true;
	}	

	@Override
	public boolean end(VersionNodeChildren children) {
		if(children.size()>1){
			prefix.pop();
			Strings.appendln(sb, prefix, "}");
		}
		return true;
	}
	

	private void printNode( AbstractNode aNode, CurrentIndent prefix, String suffix, boolean printMesg){
		RecordSummary<String> summary = aNode.getRecordSummary();
		
		Strings.appendln(sb, prefix, "[",
				Strings.alignedMillisStr(summary.getAvgBeforePeriod()), "|",
				Strings.alignedMillisStr(summary.getAvgAfterPeriod()),"]ms/",summary.getCounter(),
				suffix);
		prefix.push();
		if(printMesg){
			for(String mesg : summary.getAll()){
				Strings.appendln(sb, prefix, " - ", mesg);
			}	
		}
		prefix.pop();
	}

	public String toString(){
		return sb.toString();
	}

	/**
	 * 
	 * @author Ian YT Tsai
	 *
	 */
	private class CurrentIndent{
		private final LinkedList<String> indents;
		private final String indent;
		private int idx; 
		
		CurrentIndent(String indent){
			indents = new LinkedList<String>();
			indents.add(this.indent = indent);
		}
		
		public String push(){
			int newIdx = idx + 1;
			String newIndent = null;
			if(newIdx == indents.size()){
				indents.add(newIndent = indents.getLast()+indent);
			}else{
				newIndent = indents.get(newIdx);
			}
			idx = newIdx;
			return newIndent;
		}
		
		public String pop(){
			int newIdx = idx - 1;
			if(idx < 0)
				throw new IndexOutOfBoundsException("the indent idx could not < 0!");
			idx = newIdx;
			return toString();
		}
		
		public String toString(){
			return indents.get(idx);
		}
	}//end of class...
	
	
}
