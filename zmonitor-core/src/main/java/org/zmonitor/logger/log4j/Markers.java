/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MarkerFactory;
import org.zmonitor.MonitorMeta;
import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Markers {
	
	public static final String TRACKER_NAME_LOG4J = "log4j";
	public static final Marker MK_LOG4J;
	public static final Marker MK_PUSH_LOG4J;
	public static final Marker MK_RECORD_LOG4J;
	public static final Marker MK_END_LOG4J;
	static{
		MK_LOG4J = MarkerFactory.getMarker(TRACKER_NAME_LOG4J);
		MK_PUSH_LOG4J = MarkerFactory.getMarker("log4j-push");
		MK_PUSH_LOG4J.add(MK_LOG4J);
		
		MK_RECORD_LOG4J = MarkerFactory.getMarker("log4j-record");
		MK_RECORD_LOG4J.add(MK_LOG4J);
		
		MK_END_LOG4J = MarkerFactory.getMarker("log4j-end");
		MK_END_LOG4J.add(MK_LOG4J);
	}
	private Markers(){}

	public static Marker retrieveRegisteredMajorMarker(MonitorMeta meta) {
		String trackerName = meta.getTrackerName();
		Marker marker = meta.getMarker();
		if(TRACKER_NAME_LOG4J.equals(trackerName)){
			if(marker==null){
				return MK_LOG4J;
			}
			return marker;
		}
		throw new IllegalArgumentException(" unsupported tracker: "+trackerName);
	}
	
	
}
