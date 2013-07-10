/**
 * 
 */
package org.zmonitor;

import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public interface TrackingContext {
	
	/**
	 * 
	 * @return
	 */
	long getCreateMillis();
	/**
	 *  
	 * @return
	 */
	Object getMessage();
	/**
	 * 
	 * @param message
	 */
	void setMessage(Object message);
	/**
	 * 
	 * @return
	 */
	String getTrackerName();
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	MonitorPoint create(MonitorPoint parent);
	/**
	 * 
	 * @return
	 */
	MonitorLifecycle getLifeCycle();
	/**
	 * 
	 * @return
	 */
	MonitorMeta newMonitorMeta();
	/**
	 * 
	 * @return
	 */
	MonitorSequence getMonitorSequence();
	
	/**
	 * 
	 * @return
	 */
	StackTraceElement[] getStackTraceElements();
}
