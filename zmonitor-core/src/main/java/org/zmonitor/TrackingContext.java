/**
 * 
 */
package org.zmonitor;

import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.Name;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface TrackingContext {
	/**
	 * 
	 * @return
	 */
	Name getName();
	/**
	 * 
	 * @return
	 */
	Object getMessage();
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
