/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * 
 * Representing a logical range of two {@link MonitorPoint}s, offer user a set of operations such as:<br>
 * <ol>
 * 	<li> {@code Range#getInterval()}
 *  <li> {@code Range#greaterThan(long)}
 *  <li> {@code Range#smallerThan(long)}
 * </ol>  
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Range {

	protected final MonitorPoint current;
	protected final MonitorPoint target;
	/**
	 * 
	 * @param a
	 * @param b
	 */
	public Range(MonitorPoint current, MonitorPoint target) {
		this.current = current;
		this.target = target;
	}
	
	public MonitorPoint getCurrent() {
		return current;
	}

	public MonitorPoint getTarget() {
		return target;
	}
	/**
	 * 
	 * @return the interval milliseconds between current mp and target mp.
	 */
	public long getInterval(){
		if(target==null)return 0;
		return Math.abs(current.getCreateMillis() - target.getCreateMillis());
	}
	/**
	 * Test if the interval of this range is greater than the given millis. 
	 * @param millis
	 * @return true, if interval is greater than given millis, false otherwise.
	 */
	public boolean greaterThan(long millis){
		return getInterval() > millis;
	}
	/**
	 * 
	 * @param millis
	 * @return
	 */
	public boolean lessThan(long millis){
		return getInterval() < millis;
	}
	/**
	 * 
	 * @param startMillis
	 * @param endMillis
	 * @return
	 */
	public boolean between(long startMillis, long endMillis){
		long val = getInterval();
		return val > startMillis && val < endMillis;
	}

}
