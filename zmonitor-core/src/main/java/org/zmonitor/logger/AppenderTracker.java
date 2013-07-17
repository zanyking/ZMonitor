package org.zmonitor.logger;

import org.zmonitor.MonitorMeta;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;

/**
 * currently only log4j will use this.
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AppenderTracker extends TrackerBase {

	@Override
	public void doTrack(TrackingContext tCtx) {
		String message = (String) tCtx.getMessage();
		if(message==null||message.isEmpty()){	
			//No info to identify what's going on, should not 
			//happened because a message of Logger.log(String message) 
			//should never be null. 
			ZMonitor.record(tCtx);
		}else{
			message = message.trim();
			if(ZMonitor.isMonitoring()){
				// depends on normal operation.
			}else{
				// start a new stack for monitoring...
			}
			MonitorMeta meta = tCtx.newMonitorMeta();
			
			if(message.startsWith(pushOp)){
				if(eatOperator){
					tCtx.setMessage(message.substring(pushOp.length()));
				}
				if(meta.getMarker()==null){
					meta.setMarker(pushMarcker);
				}
				ZMonitor.push(tCtx);
			}else if(message.startsWith(popOp)){
				if(eatOperator){
					tCtx.setMessage(message.substring(popOp.length()));
				}
				if(meta.getMarker()==null){
					meta.setMarker(popMarcker);
				}
				ZMonitor.pop(tCtx);	
			}else{
				if(meta.getMarker()==null){
					meta.setMarker(trackingMarcker);
				}
				ZMonitor.record(tCtx);
			}
		}
		
	}

}
