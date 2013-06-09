/**
 * 
 */
package org.zmonitor.webtest;

import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.MonitorPointSelection;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface WebAppAssertContext {

	/**
	 * 
	 * @return
	 */
	MonitorSequence getMonitorSequence();
	
	/**
	 * 
	 * @return
	 */
	MonitorPointSelection asSelection();
}
