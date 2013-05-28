/**
 * 
 */
package org.zmonitor.selector;

import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ZMonitorSelectorFactory {
	/**
	 * 
	 * @param ms
	 * @param selector
	 * @return
	 */
	public List<MonitorPoint> find(MonitorSequence ms,  String selector);
	/**
	 * 
	 * @param mp
	 * @param selector
	 * @return
	 */
	public List<MonitorPoint> find(MonitorPoint mp,  String selector);
	
}
