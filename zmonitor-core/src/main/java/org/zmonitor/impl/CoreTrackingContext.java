/**
 * 
 */
package org.zmonitor.impl;

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

	public MonitorPoint newMonitorPoint() {
		return new MonitorPoint(this.getName(), 
				this.getMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
	}

	
	private Name getName() {
		return null;
	}

}
