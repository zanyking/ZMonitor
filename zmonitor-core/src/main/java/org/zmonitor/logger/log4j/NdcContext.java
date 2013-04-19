/**NdcContext.java
 * 2011/10/21
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.spi.Name;
import org.zmonitor.spi.MonitorSequenceLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
/*package*/ class NdcContext {

	
	private final MonitorSequenceLifecycle lfc;
	/**
	 * @param lfc
	 */
	public NdcContext(MonitorSequenceLifecycle lfc) {
		this.lfc = lfc;
	}
	
	private NdcStack fNdcStack = new NdcStack(); 
	
	
	
	public void doEnd(Name name, String mesg){
		ZMonitor.pop(name, mesg, false);
		fNdcStack.pop();
	}
	
	public void doEnd(String mesg){
		ZMonitor.pop(mesg, false);
		fNdcStack.pop();
	}
	
	public void doRecord(Name name, String mesg, int depth){
		ZMonitor.record(name, mesg, false);
//		fNdcStack.push(ndcStr, depth, getCurrentTlDepth());
	}
 
	public void doStart(Name name,String mesg, String ndcStr, int depth){
		ZMonitor.push(name, mesg, false);
		fNdcStack.push(ndcStr, depth, getCurrentTlDepth());
	}
	
	public NdcObj getNdcObj(){
		return fNdcStack.peek();
	}
	
	
	private int getCurrentTlDepth(){
		MonitorSequence tl = lfc.getMonitorSequence();
		return (tl==null)? -1 : tl.getCurrentDepth();
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	private static class NdcStack{
		private NdcObj current;
		/**
		 * @param ndcStr
		 * @param depth
		 * @param currentTlDepth
		 */
		public void push(String ndcStr, int depth, int currentTlDepth) {
			NdcObj ndcObj = new NdcObj(ndcStr, depth, currentTlDepth, current);
			current = ndcObj;
		}
		/**
		 * 
		 * @return
		 */
		public NdcObj peek() {
			return current;
		}
		/**
		 * 
		 * @return
		 */
		public NdcObj pop() {
			NdcObj temp = current;
			current = current.previous;
			return temp;
		}
	}//end of class...
	
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class NdcObj{
		public final String ndcStr;
		public final int depth;
		public final int tlDepth;
		public final NdcObj previous;
		public NdcObj(String ndcStr, int depth, int currentTlDepth, NdcObj previous) {
			super();
			this.ndcStr = ndcStr;
			this.depth = depth;
			this.tlDepth = currentTlDepth;
			this.previous = previous;
		}
		@Override
		public String toString() {
			return "NdcObj [ndcStr=" + ndcStr + ", depth=" + depth
					+ ", currentTlDepth=" + tlDepth + "]";
		}
	}//end of class...
	
	

}
