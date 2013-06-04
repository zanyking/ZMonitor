/**
 * 
 */
package org.zmonitor.util;

import org.zmonitor.MonitorPoint;

/**
 * 
 * the default implementations of {@link RangeRetriever}.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public enum RangeRetrievers implements RangeRetriever {
	/**
	 * the root mp of the monitor sequence that this mp belongs to.  
	 */
	ROOT{
		public Range retrieve(MonitorPoint mp){
			return new Range(mp, mp.getMonitorSequence().getRoot());
		}
	},
	/**
	 * 
	 */
	PARENT{
		public Range retrieve(MonitorPoint mp){
			return new Range(mp, mp.getParent());
		}
	},
	/**
	 * CASE 1: end is the latest grand-child mp of current mp.<br> 
	 *<pre>
	 *|-mp        -> CURRENT
	 *   |-mp  
	 *   |-mp
	 *      |-mp
	 *      |-mp  -> END
	 *|-mp
	 *</pre>
	 *CASE 2: end is null if this mp is a leaf.<br> 
	 *<pre>
	 *|-mp        -> CURRENT (there's no CHILD)
	 *|-mp
	 *</pre>
	 */
	END{
		public Range retrieve(MonitorPoint current){
			MonitorPoint end = (current.isLeaf()) ? 
					null : findLatestGrandChild(current.getLastChild());
			return new Range(current, end);
		}
		private MonitorPoint findLatestGrandChild(MonitorPoint current){
			return (current.isLeaf()) ? 
				current : findLatestGrandChild(current.getLastChild());
		}
	},
	/**
	 * >
	 * CASE 1: next is the next sibling if current mp has next sibling.<br>
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
	 *CASE 2: mp is the end of it's parent (or ancestor), next is the sibling of the ancestor which was ended by this mp.<br>
	 *<pre>
	 *|-mp     
	 *   |-mp
	 *   |-mp  -> THIS (the END of parent)
	 *|-mp     -> NEXT
	 *</pre>
	 *CASE 3: mp is the end of entire monitor sequence, next is NULL. <br>
	 *<pre>
	 *|-mp     
	 *   |-mp
	 *   |-mp  -> THIS
	 *(there's no NEXT)
	 *</pre>
	 */
	NEXT{
		public Range retrieve(MonitorPoint current){
			MonitorPoint next = findNext(current);
			return new Range( current, next) ;
		}
		
		private MonitorPoint findNext(MonitorPoint current){
			if(current.getParent() == null)return null;//this is a root mp, root has no next.
			
			MonitorPoint next = current.getNextSibling();
			if(next == null){
				// current is the last child of parent, 
				// search parent's next sibling.
				next = findNext( current.getParent());
			}
			return next;
		}
	},
	/**
	 * CASE 1:  mp is a middle child, previous is the previous sibling.<br>
	 * <pre>
	 *|-mp     -> PREVIOUS
	 *|-mp     -> THIS
	 *</pre>
	 * CASE 2: No matter how much child mp the previous sibling has, previous is still the previous sibling<br>
	 * <pre>
	 *|-mp     -> PREVIOUS
	 *   |-mp
	 *   |-mp
	 *|-mp     -> THIS
	 *</pre>
	 *CASE 3: mp is the first child, previous is parent.<br>
	 *<pre>
	 *|-mp     -> PREVIOUS
	 *   |-mp  -> THIS
	 *   |-mp
	 *</pre>
	 *CASE 4: mp is root, previous is null.<br>
	 *<pre>
	 *(there's no PREVIOUS)
	 *|-mp     -> THIS (root)
	 *   |-mp  
	 *   |-mp
	 *</pre>
	 */
	PREVIOUS{
		public Range retrieve(MonitorPoint current){
			MonitorPoint pre = current.getPreviousSibling(); 
			if(pre==null){
				pre = current.getParent();
			}
			return new Range(current, pre); 
		}
	};
}
