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
	/**
	 * the default implementations of {@link RangeRetriever}.
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public enum Default implements RangeRetriever{
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
		 * A Broad First Search range retriever.<br>
		 * Next mp is the next sibling if current mp has next sibling (based on BFS algorithm).<br>
		 * <pre>
		 *|-mp     -> CURRENT
		 *   |-mp
		 *   |-mp
		 *|-mp     -> NEXT
		 *</pre>
		 * current mp is the end of it's parent (or ancestor), next mp is the next sibling of the ancestor which was ended by current mp.<br>
		 *<pre>
		 *|-mp     -> ancestor
		 *   |-mp
		 *   |-mp  -> CURRENT (the END of parent)
		 *|-mp     -> NEXT
		 *</pre>
		 *CASE 3: current mp is the end of entire monitor sequence, next mp is null. <br>
		 *<pre>
		 *|-mp     
		 *   |-mp
		 *   |-mp  -> CURRENT
		 *(there's no NEXT)
		 *</pre>
		 */
		NEXT{
			public Range retrieve(MonitorPoint current){
				MonitorPoint next = BFS(current);
				return new Range( current, next) ;
			}
			
			private MonitorPoint BFS(MonitorPoint current){
				if(current.getParent() == null)return null;//this is a root mp, root has no next.
				
				MonitorPoint next = current.getNextSibling();
				if(next == null){
					// current is the last child of parent, 
					// search parent's next sibling.
					next = BFS( current.getParent());
				}
				return next;
			}
		},
		/**
		 * Current mp is a middle child, previous mp is the previous sibling.<br>
		 *<pre>
		 *|-mp     -> PREVIOUS
		 *   |-mp
		 *   |-mp
		 *|-mp     -> CURRENT
		 *</pre>
		 * Current mp is the first child of its parent, previous mp is current mp's parent.<br>
		 *<pre>
		 *|-mp     -> PREVIOUS
		 *   |-mp  -> CURRENT
		 *   |-mp
		 *</pre>
		 * Current mp is root, previous is null.<br>
		 *<pre>
		 *(there's no PREVIOUS)
		 *|-mp     -> CURRENT (root)
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
		},
		/**
		 * A Depth First Search range retriever.<br>
		 * 
		 * Next mp is the first child of current mp. (based on DFS algorithm).<br>
		 * <pre>
		 *|-mp     -> CURRENT
		 *   |-mp  -> NEXT
		 *   |-mp
		 *|-mp     
		 *</pre>
		 *
		 * Next mp is the next sibling if current mp has no child.<br>
		 * <pre>
		 *|-mp     -> CURRENT
		 *|-mp     -> NEXT
		 *</pre>
		 *
		 * Current mp is the end of it's parent (or ancestor), next mp is the next sibling of the ancestor which was ended by current mp.<br>
		 *<pre>
		 *|-mp     -> ancestor
		 *   |-mp
		 *   |-mp  -> CURRENT (the END of parent)
		 *|-mp     -> NEXT
		 *</pre>
		 *
		 * Current mp is the end of entire monitor sequence, next mp is NULL. <br>
		 *<pre>
		 *|-mp     
		 *   |-mp
		 *   |-mp  -> CURRENT
		 *(there's no NEXT)
		 *</pre>
		 */
		STRICT_NEXT{
			public Range retrieve(MonitorPoint current){
				if(current.getParent()==null){// this is root mp...
					return END.retrieve(current);
				}
				MonitorPoint next = dfs(current, current);
				return new Range( current, next) ;
			}
			private MonitorPoint dfs(MonitorPoint start, MonitorPoint current){
				MonitorPoint next = current.getFirstChild();

				while(next == null){//has no first child, look up next sibling...
					next = current.getNextSibling();
					if(next ==null){//current is the last child of parent, looking up parent's next sibling.
						current = current.getParent();
						if(current == null || current == start){// reach the end...
							return null;
						}
					}
				}
				return next;
			}
		}
	}
}
