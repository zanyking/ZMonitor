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
	 * the elapsed time between current and the strict next mp.<br>
	 * A strict next mp means:<br>
	 * <ul>
	 * <li> if mp is root, it's the very end of current.
	 * <li> if mp has first child, it's the first child 
	 * <li> the next sibling if any 
	 * <li> null, if the current is the very end of the entire mp tree.
	 * <ul>
	 * 
	 * @param current
	 * @return
	 */
	public static long retrieveMillisToStrictNext(MonitorPoint current){
		return RangeRetriever.Default.STRICT_NEXT.retrieve(current).getInterval();
	}
	
	/**
	 * calculate the elapsed time from current to next(see: {@link #getNext(MonitorPoint)}) in milliseconds.   
	 * @param current
	 * @return if the next mp of current is not null, return next.getCreateMillis() - current.getCreateMillis(), 
	 * 0 otherwise.
	 */
	public static long retrieveMillisToNext(MonitorPoint current){
		return RangeRetriever.Default.NEXT.retrieve(current).getInterval();
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
		return RangeRetriever.Default.PREVIOUS.retrieve(current).getInterval();
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
		return RangeRetriever.Default.END.retrieve(current).getInterval();
	}
	

}
