/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.MonitorMeta;
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
	
	protected MonitorMeta monitorMeta;
	
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
	
	public long getCreateMillis() {
		return createMillis;
	}


	public void setCreateMillis(long createMillis) {
		this.createMillis = createMillis;
	}


	public String getTrackerName() {
		return trackerName;
	}

	public void setMonitorMeta(MonitorMeta cInfo) {
		monitorMeta = cInfo;
	}
	
	public MonitorMeta getMonitorMeta(){
		return monitorMeta;
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
		MonitorPoint mp = new MonitorPoint(
				this.getMonitorMeta(),
				this.getMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
		if(parent!=null) mp.setParent(parent);
		return mp;
	}
}
