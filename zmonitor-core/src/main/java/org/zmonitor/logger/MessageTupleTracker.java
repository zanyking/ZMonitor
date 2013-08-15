/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;

/**
 * 
 * need to refactor if have time.
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MessageTupleTracker extends TrackerBase {
	private MessageTrackingHelper helper = new MessageTrackingHelper();
	
	public void doTrack(TrackingContext tCtx) {
//		helper.track(tCtx);	
		if(tCtx.getStackTraceElements()==null){
			helper.track(tCtx);	
		}else{
			new AutoTrackingHelper(helper).track(tCtx); 
		}
	}
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public class MessageTrackingHelper {
		public void track(TrackingContext tCtx) {
			MessageTuple mt = (MessageTuple) tCtx.getMessage();
			String mesg = mt.getMessagePattern();

			if (mesg == null) { // No info to identify what's going on, should
								// not
				// happened because a message of Logger.log(String message)
				// should never be null.
				ZMonitor.pinpoint(tCtx);
			} else {
				if (mesg.startsWith(pushOp)) {
					if (eatOperator) {
						mt.setMessagePattern(mesg.substring(pushOp.length()));
					}
					ZMonitor.push(tCtx);
				} else if (mesg.startsWith(popOp)) {
					if (eatOperator) {
						mt.setMessagePattern(mesg.substring(popOp.length()));
					}
					ZMonitor.pop(tCtx);
				} else {
					ZMonitor.pinpoint(tCtx);
				}
			}
		}
	}//end of class...
	
}
