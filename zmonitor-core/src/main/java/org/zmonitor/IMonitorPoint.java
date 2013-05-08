/**
 * 
 */
package org.zmonitor;

import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface IMonitorPoint {
	/**
	 * 
	 * @return
	 */
	MonitorSequence getMonitorSequence();
	/**
	 * 
	 * @return
	 */
	long getCreateMillis();
	/**
	 * 
	 * @return
	 */
	List<MonitorPoint> getChildren();
	/**
	 * 
	 * @return
	 */
	int size();
}
