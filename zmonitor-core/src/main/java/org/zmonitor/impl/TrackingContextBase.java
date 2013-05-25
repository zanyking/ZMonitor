/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.CallerInfo;
import org.zmonitor.Marker;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TrackingContextBase implements TrackingContext {

	protected Object message;
	
	protected final String trackerName;
	
	protected CallerInfo callerInfo;
	
	protected Marker marker;
	
	protected long createMillis = System.currentTimeMillis();
	
	
	/**
	 * 
	 * @param trackerName
	 */
	public TrackingContextBase(String trackerName) {
		this.trackerName = trackerName;
	}

	
	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public long getCreateMillis() {
		return createMillis;
	}


	public void setCreateMillis(long createMillis) {
		this.createMillis = createMillis;
	}


	public String getTrackerName() {
		return trackerName;
	}

	public void setCallerInfo(CallerInfo cInfo) {
		callerInfo = cInfo;
	}
	
	public CallerInfo getCallerInfo(){
		return callerInfo;
	}

	/**
	 * override this method in your tracking environment to get monitorlifecycle back.
	 */
	public MonitorLifecycle getLifeCycle() {
		return ZMonitorManager.getInstance().getMonitorLifecycle();
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.TrackingContext#getMonitorSequence()
	 */
	public MonitorSequence getMonitorSequence() {
		return getLifeCycle().getInstance();
	}

	public MonitorPoint create(MonitorPoint parent) {
		MonitorPoint mp = new MonitorPoint(this.getMarker(), 
				this.getCallerInfo(),
				this.getMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
		return mp;
	}
}
