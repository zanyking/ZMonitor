/**
 * 
 */
package org.zmonitor.slf4j;

import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.logger.TrackerBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Slf4jTracker extends TrackerBase {

	public void tracking(TrackingContext tCtx) {
		MessageTuple mt = (MessageTuple) tCtx.getMessage();
		String mesg = mt.getMessagePattern();
		
		if(mesg==null){	//No info to identify what's going on, should not 
						//happened because a message of Logger.log(String message) 
						//should never be null. 
			ZMonitor.record(tCtx);
		}else{
			if(mesg.startsWith(pushOp)){
				if(eatOperator){
					mt.setMessagePattern(mesg.substring(pushOp.length()));
				}
				ZMonitor.push(tCtx);
			}else if(mesg.startsWith(popOp)){
				if(eatOperator){
					mt.setMessagePattern(mesg.substring(popOp.length()));
				}
				ZMonitor.pop(tCtx);	
			}else{
				ZMonitor.record(tCtx);
			}
		}
	}
}
