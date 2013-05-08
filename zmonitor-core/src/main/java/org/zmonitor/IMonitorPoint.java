/**
 * 
 */
package org.zmonitor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface IMonitorPoint extends Serializable{
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
