/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.CallerInfo;
import org.zmonitor.MonitorPoint;
import org.zmonitor.spi.Name;

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
		// TODO Auto-generated constructor stub
	}

	public MonitorPoint create(MonitorPoint parent) {
		MonitorPoint mp = new MonitorPoint(this.retrieveName(), 
				getCallerInfo(),
				retrieveMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
		if(parent!=null)
			mp.setParent(parent);
		
		return  mp;
	}

	
	private CallerInfo getCallerInfo(){
		StackTraceElement stEle = this.getStackTraceElement();
		return stEle==null? null :
				new CallerInfo( stEle);
	}
	
	private Name retrieveName() {
		return new StringName("default");
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
