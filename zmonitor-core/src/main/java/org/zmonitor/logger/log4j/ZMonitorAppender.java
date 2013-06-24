/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorAppender extends ZMonitorAppenderBase {

	@Override
	protected void append(LoggingEvent event) {
		String ndcStr = null;
		int depth = -1;
		{//IMPORTANT: this section is something MUST be called!
			ndcStr = event.getNDC();
		    depth = NDC.getDepth();
		    event.getThreadName();
		    // Get a copy of this thread's MDC.
		    event.getMDCCopy();
		}
	    
	    if(preventRecursion(this.getClass(), event.getLoggerName()) || 
	    		preventRecursion(ZMLog.class, event.getLoggerName()))
	    	return;
	    {//IMPORTANT: this section is something MUST be called!
	    	String mesg = event.getRenderedMessage();
	    	event.getThrowableStrRep();
	    	MonitorLifecycle lfc = ZMonitorManager.getInstance().getMonitorLifecycle();
	    	if(ZMonitor.isMonitoring()){
	    		
	    	}else{
	    		// start new life-cycle ? 
	    	}
	    }
	}
	
}
