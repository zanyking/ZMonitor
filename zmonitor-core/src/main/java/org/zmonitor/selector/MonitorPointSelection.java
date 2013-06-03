/**
 * 
 */
package org.zmonitor.selector;

import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorPointSelection extends Selection<MonitorPoint> {

	MonitorPointSelection greaterThan();
}
