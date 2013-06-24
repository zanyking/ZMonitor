/**
 * 
 */
package org.zmonitor.marker;

import org.zmonitor.MarkerFactory;
import org.zmonitor.MonitorMeta;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Markers {
	private Markers() {}

	public static final Marker MK_ZM;
	public static final Marker MK_PUSH_ZM;
	public static final Marker MK_RECORD_ZM;
	public static final Marker MK_END_ZM;
	public static final String TRACKER_NAME_ZM = "zmonitor";
	static{
		MK_ZM = MarkerFactory.getMarker("zm");
		MK_PUSH_ZM = MarkerFactory.getMarker("push");
		MK_PUSH_ZM.add(MK_ZM);
		
		MK_RECORD_ZM = MarkerFactory.getMarker("record");
		MK_RECORD_ZM.add(MK_ZM);
		
		MK_END_ZM = MarkerFactory.getMarker("end");
		MK_END_ZM.add(MK_ZM);
	}
	
	
	public static final Marker MK_ZM_WEB;
	public static final Marker MK_PUSH_ZM_WEB;
	public static final Marker MK_RECORD_ZM_WEB;
	public static final Marker MK_END_ZM_WEB;
	public static final String TRACKER_NAME_ZM_WEB = "web";
	static{
		MK_ZM_WEB = MarkerFactory.getMarker(TRACKER_NAME_ZM_WEB);
		MK_PUSH_ZM_WEB  = MarkerFactory.getMarker("web-push");
		MK_PUSH_ZM_WEB .add(MK_ZM_WEB );
		
		MK_RECORD_ZM_WEB  = MarkerFactory.getMarker("web-record");
		MK_RECORD_ZM_WEB .add(MK_ZM_WEB );
		
		MK_END_ZM_WEB  = MarkerFactory.getMarker("web-end");
		MK_END_ZM_WEB .add(MK_ZM_WEB );
	}
	
	/**
	 * must return value, will throw IllegalArgumentException if given MonitorMeta's trackerName is unknown.
	 * @param meta
	 * @return
	 */
	public static Marker retrieveRegisteredMajorMarker(MonitorMeta meta) {
		
		String trackerName = meta.getTrackerName();
		Marker marker = meta.getMarker();
		
		if(TRACKER_NAME_ZM.equals(trackerName)){
			if(marker==null){
				return MK_ZM;
			}
			return marker;// a marker which conceptual tracker is: org.zmonitor.ZMnotitor.
		}
		
		if(TRACKER_NAME_ZM_WEB.equals(trackerName)){
			if(marker==null){
				return MK_ZM_WEB;
			}
			return marker;// a marker which conceptual tracker is: org.zmonitor.web.ZMonitorServletFilter. 
		}
		
		throw new IllegalArgumentException(" unsupported tracker: "+trackerName);
	}
	
}
