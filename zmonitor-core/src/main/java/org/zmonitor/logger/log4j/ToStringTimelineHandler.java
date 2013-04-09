/**ToStringTimelineHandler.java
 * 2011/4/7
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zmonitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ToStringTimelineHandler extends org.zmonitor.handler.ToStringTimelineHandler{
	private Logger logger = Logger.getLogger(ZMonitor.class);

	@Override
	protected void toString(String message) {
		logger.debug(message);
	}
	
	
	
	
	
	
}
