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
	 * @return
	 */
	String getTrackerName();
	
	/**
	 * 
	 * @return
	 */
	MonitorPoint newMonitorPoint();
	/**
	 * 
	 * @return
	 */
	StackTraceElement getMonitorPointStackTraceElement();
	/**
	 * 
	 * @return
	 */
	MonitorLifecycle getLifeCycle();
	/**
	 * 
	 * @return
	 */
	MonitorSequence getMonitorSequence();
}
