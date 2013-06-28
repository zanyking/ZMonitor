package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorMeta;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.logger.TrackerBase;
import org.zmonitor.marker.Marker;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AppenderTracker extends TrackerBase {

	@Override
	public void tracking(TrackingContext tCtx) {
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
			MonitorMeta meta = tCtx.getMonitorMeta();
			
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
