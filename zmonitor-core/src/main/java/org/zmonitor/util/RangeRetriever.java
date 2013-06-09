/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * 
 * <p>
 * An implementation of {@link RangeRetriever} will based on it's conceptual
 * meaning to retrieve a range according to given target.

 * <p>
 * If you want to take a look at an example of this interface or simply use the
 * pre-build class, please refers to: {@link RangeRetrievers}.<br>
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
