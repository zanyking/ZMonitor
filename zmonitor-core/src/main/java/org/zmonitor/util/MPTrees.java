/**
 * 
 */
package org.zmonitor.util;

import static org.zmonitor.util.MPTrees.retrieveElapsedMillisToNext;

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
	public static long retrieveElapsedMillisToNext(MonitorPoint current){
		
		MonitorPoint next = searchNext(current);
		if(next==null)return 0;
		return next.getCreateMillis() - current.getCreateMillis();
	}
	private static MonitorPoint searchNext(MonitorPoint current){
		if(current.getParent() == null)return null;//this is a root mp.
		
		MonitorPoint next = null;
		if(current.getNextSibling() == null){
			// current is the last child of parent, 
			// search parent's next sibling.
			next = searchNext( current.getParent());
		}else{
			next = current.getNextSibling();
		}
		return next;
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
	public static long retrieveElapsedMillisFromPrevious(MonitorPoint current){
		MonitorPoint pre = current.getPreviousSibling(); 
		if(pre==null){
			pre = current.getParent();
		}
		if(pre==null){
			return 0;
		}
		return current.getCreateMillis() - pre.getCreateMillis();
	}
	
	/**
	 * 
	 * @param current
	 * @return
	 */
	public static long getElapsedMillis(MonitorPoint current){
		long self = retrieveElapsedMillisToNext(current);
		if(current.isLeaf()) return self;
		return self - (current.getLastMillis() - current.getFirstChild().getCreateMillis());
	}
	
	/**
	 * 
	 * @param current
	 * @return
	 */
	public static long getBranchElipsedByEndTag(MonitorPoint current){
		if(current.isLeaf()){//is leaf
			return 0;
		}
		return current.getLastMillis() - current.getCreateMillis();
	}
	
}
