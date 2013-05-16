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
	 * @param parent
	 * @return
	 */
	MonitorPoint create(MonitorPoint parent);
	/**
	 * 
	 * @return
	 */
	StackTraceElement getStackTraceElement();
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
