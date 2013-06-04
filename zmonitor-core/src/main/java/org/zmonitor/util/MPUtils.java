/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPUtils {

	private MPUtils() {}
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 */
	
	
	
	/**
	 * 
	 * @param target
	 * @param current
	 * @return
	 */
	public static Range get(RangeRetriever target, MonitorPoint current){
		return target.retrieve(current);
	}
	
	
	
	
	/**
	 * calculate the elapsed time from current to next(see: {@link #getNext(MonitorPoint)}) in milliseconds.   
	 * @param current
	 * @return if the next mp of current is not null, return next.getCreateMillis() - current.getCreateMillis(), 
	 * 0 otherwise.
	 */
	public static long retrieveMillisToNext(MonitorPoint current){
		return RangeRetrievers.NEXT.retrieve(current).getInterval();
	}
	/**
	 * retrieve the elapsed time from previous mp(see: {@link #getPrevious(MonitorPoint)}) to current mp.  
	 * <pre>
	 *|-mp     -> PREVIOUS
	 *|-mp     -> THIS
	 *</pre>
	 *
	 * @param current
	 * @return the elapsed time in milliseconds
	 */
	public static long retrieveMillisToPrevious(MonitorPoint current){
		return RangeRetrievers.PREVIOUS.retrieve(current).getInterval();
	}
	
	/**
	 * the elapsed time from very-end to next.
	 *<pre>
	 *|-mp        -> CURRENT
	 *   |-mp  
	 *   |-mp
	 *      |-mp
	 *      |-mp  -> VERY END
	 *|-mp       
	 *</pre>
	 * @param current
	 * @return
	 */
	public static long retrieveMillisToEnd(MonitorPoint current){
		return RangeRetrievers.END.retrieve(current).getInterval();
	}
	

}
