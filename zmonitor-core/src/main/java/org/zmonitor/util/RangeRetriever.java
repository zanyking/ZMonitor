/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * If you want to take a look at an example of this interface or 
 * simply use the pre-build class, please refers to: {@link RangeRetrievers}.<br>
 * 
 * <p>
 * a range retriever will based on it's {@code #retrieve(MonitorPoint)} 
 * method to get a target {@code MonitorPoint} and create a range 
 * between the given mp to target mp. 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface RangeRetriever {
	/**
	 * get a Range from current mp to target mp.
	 * @param current
	 * @return a range of current to target, never return null.
	 */
	public Range retrieve(MonitorPoint current);
}
