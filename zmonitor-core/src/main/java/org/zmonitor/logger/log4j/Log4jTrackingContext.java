/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorPoint;
import org.zmonitor.impl.TrackingContextBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4jTrackingContext extends TrackingContextBase {

	
	public Log4jTrackingContext(String trackerName) {
		super(trackerName);
	}

	public MonitorPoint create(MonitorPoint parent) {
		return null;//TODO not implement yet
	}


}
