/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class TrackingContextBase implements TrackingContext {

	protected Object message;
	protected long createMillis = System.currentTimeMillis();
	protected final String trackerName;
	protected final StackTraceElement[] stackTraceElements;
	/**
	 * 
	 * @param trackerName
	 */
	public TrackingContextBase(String trackerName, StackTraceElement[] stackTraceElements) {
		this.trackerName = trackerName;
		this.stackTraceElements = stackTraceElements;
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

	public StackTraceElement[] getStackTraceElements() {
		return stackTraceElements;
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
		return getLifeCycle().getMonitorSequence();
	}

	public MonitorPoint create(MonitorPoint parent) {
		MonitorPoint mp = new MonitorPoint(
				this.newMonitorMeta(),
				this.getMessage(), 
				this.getMonitorSequence(), 
				this.getCreateMillis()
				);
		getLifeCycle().getState().increament();
		if(parent!=null) mp.setParent(parent);
		return mp;
	}

	

}
