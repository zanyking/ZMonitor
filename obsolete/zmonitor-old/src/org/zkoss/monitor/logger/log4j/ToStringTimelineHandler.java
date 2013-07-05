/**ToStringTimelineHandler.java
 * 2011/4/7
 * 
 */
package org.zkoss.monitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zkoss.monitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ToStringTimelineHandler extends org.zkoss.monitor.handler.ToStringTimelineHandler{
	private Logger logger = Logger.getLogger(ZMonitor.class);

	@Override
	protected void toString(String message) {
		logger.debug(message);
	}
	
	
	
	
	
	
}
