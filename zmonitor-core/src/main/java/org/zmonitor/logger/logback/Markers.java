/**
 * 
 */
package org.zmonitor.logger.logback;

import org.zmonitor.MarkerFactory;
import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Markers {
	private Markers(){}
	
	public static final String TRACKER_NAME_LOGBACK = "logback";
	public static final Marker MK_LOGBACK;
	public static final Marker MK_PUSH_LOGBACK;
	public static final Marker MK_RECORD_LOGBACK;
	public static final Marker MK_END_LOGBACK;
	static{
		MK_LOGBACK = MarkerFactory.getMarker(TRACKER_NAME_LOGBACK);
		MK_PUSH_LOGBACK = MarkerFactory.getMarker("logback-push");
		MK_PUSH_LOGBACK.add(MK_LOGBACK);
		
		MK_RECORD_LOGBACK = MarkerFactory.getMarker("logback-record");
		MK_RECORD_LOGBACK.add(MK_LOGBACK);
		
		MK_END_LOGBACK = MarkerFactory.getMarker("logback-end");
		MK_END_LOGBACK.add(MK_LOGBACK);
	}
	
	
}
