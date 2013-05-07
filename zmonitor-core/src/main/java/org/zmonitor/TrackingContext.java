/**
 * 
 */
package org.zmonitor;

import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.Name;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface TrackingContext {
	Name getName(); 
	Object getMessage();
	StackTraceElement getMonitorPointStackTraceElement();
	MonitorLifecycle getLifeCycle();
	MonitorSequence getMonitorSequence();
}
