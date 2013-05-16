/**
 * 
 */
package org.zmonitor.impl;

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
	
	protected final String trackerName;
	
	protected StackTraceElement callerSTElement;
	
	protected long createMillis;
	
	
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

	public void setCallerSTElement(StackTraceElement callerSTElement) {
		this.callerSTElement = callerSTElement;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.TrackingContext#getMonitorPointStackTraceElement()
	 */
	public StackTraceElement getMonitorPointStackTraceElement() {
		return callerSTElement;
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

}
