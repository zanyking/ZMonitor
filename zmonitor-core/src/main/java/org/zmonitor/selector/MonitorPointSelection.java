/**
 * 
 */
package org.zmonitor.selector;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.RangeRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorPointSelection extends Selection<MonitorPoint, MonitorPointSelection> {
	/**
	 * 
	 * @param target
	 * @param millis
	 * @return
	 */
	MonitorPointSelection greaterThan(RangeRetriever retriever, long millis);
	
	/**
	 * 
	 * @param target
	 * @param millis
	 * @return
	 */
	MonitorPointSelection smallerThan(RangeRetriever retriever, long millis);
	
	/**
	 * 
	 * @param target
	 * @param startMillis
	 * @param endMillis
	 * @return
	 */
	MonitorPointSelection between(RangeRetriever retriever, long startMillis, long endMillis);
}
