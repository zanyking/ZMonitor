/**NdcContext.java
 * 2011/10/21
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
/*package*/ class NdcContext {

	
	private final MonitorLifecycle lfc;
	/**
	 * @param lfc
	 */
	public NdcContext(MonitorLifecycle lfc) {
		this.lfc = lfc;
	}
	
	private NdcStack fNdcStack = new NdcStack(); 
	
	public void doRecord(TrackingContext ctx, int depth){
		ZMonitor.record(ctx);
//		fNdcStack.push(ndcStr, depth, getCurrentTlDepth());
	}
 
	public void doStart(TrackingContext ctx, String ndcStr, int depth){
		ZMonitor.push(ctx);
		fNdcStack.push(ndcStr, depth, getCurrentTlDepth());
	}
	public void doEnd(TrackingContext ctx){
		fNdcStack.pop();
		ZMonitor.pop(ctx);
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
//			System.out.printf("NdcStack::push() ndcStr=%1$2s,depth=%2$2s, currentTlDepth=%3$2s \n" ,
//					ndcStr,depth,currentTlDepth);
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
//			System.out.printf("NdcStack::poped() old=%1$2s,  new=%2$2s \n" ,
//					temp, current);
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
