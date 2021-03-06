/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.TrackerBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorAppender extends ZMonitorAppenderBase {

	@Override
	protected void append(LoggingEvent event) {
		{//IMPORTANT: this section is something MUST be called!
			event.getNDC();
		    NDC.getDepth();
		    event.getThreadName();
		    // Get a copy of this thread's MDC.
		    event.getMDCCopy();
		}
	    
	    if(preventRecursion(this.getClass(), event.getLoggerName()) || 
	    		preventRecursion(ZMLog.class, event.getLoggerName()))
	    	return;
	    {//IMPORTANT: this section is something MUST be called!
	    	event.getRenderedMessage();
	    	event.getThrowableStrRep();
//	    	MonitorLifecycle lfc = ZMonitorManager.getInstance().getMonitorLifecycle();
	    	TrackerBase tracker = Log4jConfigurator.getInstance().getTracker();
	    	
	    	tracker.doTrack(newTrackingContext(event, null, event.getThreadName()));
	    	
	    }
	}
	
}
