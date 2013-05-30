/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPTrees {

	private MPTrees() {}
	
	
	/**
	 * Retrieve the elapsed milliseconds to the next mp. <br>
	 * CASE 1: simple, just get next sibling.<br>
	 * <pre>
	 *|-mp     -> THIS
	 *|-mp     -> NEXT
	 *</pre>
	 * <pre>
	 *|-mp     -> THIS
	 *   |-mp
	 *   |-mp
	 *|-mp     -> NEXT
	 *</pre>
	 *CASE 2: complex, has to look up the nearest ancestor 
	 *who has the next sibling (which means it's not an end).<br>
	 *<pre>
	 *|-mp     
	 *   |-mp
	 *   |-mp  -> THIS
	 *|-mp     -> NEXT
	 *</pre>
	 *CASE 3: there's no next mp, the answer is zero. <br>
	 *<pre>
	 *|-mp     
	 *   |-mp
	 *   |-mp  -> THIS
	 *(there's no NEXT)
	 *</pre>
	 * @return the elapsed time to next mp in milliseconds.
	 */
	public static MonitorPoint getNext(MonitorPoint current){
		if(current.getParent() == null)return null;//this is a root mp.
		
		MonitorPoint next = null;
		if(current.getNextSibling() == null){
			// current is the last child of parent, 
			// search parent's next sibling.
			next = getNext( current.getParent());
		}else{
			next = current.getNextSibling();
		}
		return next;
	}
	
	
	/**
	 * calculate the elapsed time from current to next(see: {@link #getNext(MonitorPoint)}) in milliseconds.   
	 * @param current
	 * @return if the next mp of current is not null, return next.getCreateMillis() - current.getCreateMillis(), 
	 * 0 otherwise.
	 */
	public static long retrieveMillisToNext(MonitorPoint current){
		MonitorPoint next = getNext(current);
		if(next==null)return 0;
		return next.getCreateMillis() - current.getCreateMillis();
	}
	/**
	 * CASE 1: simple, just get the previous sibling.<br>
	 * <pre>
	 *|-mp     -> PREVIOUS
	 *|-mp     -> THIS
	 *</pre>
	 *CASE 2: don't care, still get the previous sibling<br>
	 * <pre>
	 *|-mp     -> PREVIOUS
	 *   |-mp
	 *   |-mp
	 *|-mp     -> THIS
	 *</pre>
	 *CASE 3: the first child, use parent as previous.<br>
	 *<pre>
	 *|-mp     -> PREVIOUS
	 *   |-mp  -> THIS
	 *   |-mp
	 *</pre>
	 * @return the elapsed time to next mp in milliseconds.
	 */
	public static MonitorPoint getPrevious(MonitorPoint current){
		MonitorPoint pre = current.getPreviousSibling(); 
		if(pre==null){
			pre = current.getParent();
		}
		return pre; 
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
		MonitorPoint pre = getPrevious(current); 
		if(pre==null)return 0;
		return current.getCreateMillis() - pre.getCreateMillis();
	}
	
	/**
	 * get the latest mp of current mp.<br> 
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
	public static MonitorPoint getVeryEnd(MonitorPoint current){
		MonitorPoint lastChild = current.getLastChild();
		return (lastChild==null)? current : getVeryEnd(lastChild);
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
	public static long retrieveMillisToVeryEnd(MonitorPoint current){
		if(current.isLeaf()){//is leaf
			return 0;
		}
		return getVeryEnd(current).getCreateMillis() - current.getCreateMillis();
	}
	
	/**
	 * the elapsed time from very-end to next.
	 *<pre>
	 *|-mp        -> CURRENT
	 *   |-mp  
	 *   |-mp
	 *      |-mp
	 *      |-mp  -> VERY END
	 *|-mp        -> NEXT
	 *</pre>
	 * @param current
	 * @return
	 */
	public static long retrieveMillisFromVeryEnd2Next(MonitorPoint current){
		long self = retrieveMillisToNext(current);
		if(current.isLeaf()) return self;
		return self - (getVeryEnd(current).getCreateMillis() - current.getFirstChild().getCreateMillis());
	}

}
