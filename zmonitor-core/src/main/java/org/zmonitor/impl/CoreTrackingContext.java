/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CoreTrackingContext extends TrackingContextBase {

	

	/**
	 * @param trackerName
	 */
	public CoreTrackingContext(String trackerName) {
		super(trackerName);
	}

	public MonitorPoint create(MonitorPoint parent) {
		MonitorPoint mp = new MonitorPoint(
				this.getMonitorMeta(),
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
			MonitorMeta cInfo = getMonitorMeta();
			if(cInfo!=null){
				return cInfo.toString();
			}
		}
		return "";
	}
}
