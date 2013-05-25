/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.CallerInfo;
import org.zmonitor.Marker;
import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CoreTrackingContext extends TrackingContextBase {

	

	/**
	 * @param trackerName
	 */
	public CoreTrackingContext(String trackerName, Marker marker) {
		super(trackerName);
		this.marker = marker;
	}

	public MonitorPoint create(MonitorPoint parent) {
		MonitorPoint mp = new MonitorPoint(this.getMarker(), 
				this.getCallerInfo(),
				this.retrieveMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
		if(parent!=null)
			mp.setParent(parent);
		
		return  mp;
	}

	
	private Object retrieveMessage() {
		Object mesg = this.getMessage();
		if(mesg==null){
			CallerInfo cInfo = getCallerInfo();
			if(cInfo!=null){
				return cInfo.toString();
			}
		}
		return "";
	}
}
