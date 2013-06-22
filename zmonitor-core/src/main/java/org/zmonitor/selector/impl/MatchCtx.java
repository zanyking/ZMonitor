/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.SelSequence;

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
	
	public MatchCtx<E> toFirstChild();
	
	public MatchCtx<E> toNextSibling();
	
	public boolean isMatched();
	
	/**
	 * Return the Entry.
	 */
	public Entry<E> getEntry();
	
}
