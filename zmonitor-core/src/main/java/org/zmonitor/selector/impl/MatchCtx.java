/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;

/**
 * A wrapper of Entry, providing a context for selector matching algorithm.
 * @author simonpai, Ian YT Tsai(Zanyking)
 */
public interface MatchCtx<E>{
	
	
	// getter //
	/**
	 * Return the parent context
	 */
	public MatchCtx<E> getParent();
	
	/**
	 * Return the Entry.
	 */
	public Entry<E> getEntry();
	

	/**
	 * Return the child index of the Entry. If the Entry is one of the 
	 * page roots, return -1.
	 */
	public int getChildIndex();
	
	/**
	 * Return the count of total siblings of the Entry, including itself.
	 * @return
	 */
	public int getSiblingSize();
	
	
	
	// match position //
	/**
	 * Return true if the Entry matched the given position of the given 
	 * selector.
	 * @param selector
	 * @param position
	 * @return
	 */
	public boolean isQualified(int selectorIndex, int position);
	
//	/*package*/ void setQualified(int selectorIndex, int position);
//	
//	/*package*/ void setQualified(int selectorIndex, int position, 
//			boolean qualified) ;
	
	/**
	 * Return true if the Entry matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 * @return
	 */
	public boolean isMatched() ;
	
	/**
	 * Return true if the Entry matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 * @return
	 */
	public boolean isMatched(int selectorIndex) ;
	
	
	
	// match local property //
	/**
	 * Return true if the Entry qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 * @return
	 */
	boolean match(SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs);
	
	
}
