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
public interface AssertContext {

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
