/**AbstractNode.java
 * 2011/3/14
 * 
 */
package org.zkoss.monitor.vtree;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class AbstractNode {
	@SuppressWarnings("rawtypes")
	private RecordSummary recordSummary;

	@SuppressWarnings("unchecked")
	public <T> RecordSummary<T> getRecordSummary() {
		return recordSummary;
	}

	public <T> void setRecordSummary(RecordSummary<T> recordSummary) {
		this.recordSummary = recordSummary;
	}
	
	
}
