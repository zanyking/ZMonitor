/**
 * 
 */
package org.zmonitor.slf4j;

import org.zmonitor.MarkerFactory;
import org.zmonitor.MonitorMeta;
import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Markers {
	private Markers() {}
	public static final Marker MK_SLF4J;
	public static final Marker MK_PUSH_SLF4J;
	public static final Marker MK_RECORD_SLF4J;
	public static final Marker MK_END_SLF4J;
	public static final String TRACKER_NAME_SLF4J = "slf4j";
	static{
		MK_SLF4J = MarkerFactory.getMarker(TRACKER_NAME_SLF4J);
		MK_PUSH_SLF4J = MarkerFactory.getMarker("slf4j-push");
		MK_PUSH_SLF4J.add(MK_SLF4J);
		
		MK_RECORD_SLF4J = MarkerFactory.getMarker("slf4j-record");
		MK_RECORD_SLF4J.add(MK_SLF4J);
		
		MK_END_SLF4J = MarkerFactory.getMarker("slf4j-end");
		MK_END_SLF4J.add(MK_SLF4J);
	}
	/**
	 * 
	 * @param meta
	 * @return
	 */
	public static Marker retrieveRegisteredMajorMarker(MonitorMeta meta) {
		
		String trackerName = meta.getTrackerName();
		Marker marker = meta.getMarker();
		
		if(TRACKER_NAME_SLF4J.equals(trackerName)){
			if(marker==null){
				return MK_SLF4J;
			}
			return marker;// a marker which conceptual tracker is: org.zmonitor.ZMnotitor.
		}
		throw new IllegalArgumentException(" unsupported tracker: "+trackerName);
	}
}
