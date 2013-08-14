/**
 * 
 */
package org.zmonitor.selector.impl.model;

import java.util.ArrayList;

import org.zmonitor.selector.impl.ParseException;

/**
 * The model representing a selector.
 * @author simonpai
 */
public class Selector extends ArrayList<SelSequence> {
	
	private static final long serialVersionUID = -9125226126564264333L;
	
	private final int _selectorIndex;
	
	public Selector(int selectorIndex){
		_selectorIndex = selectorIndex;
	}
	
	/**
	 * Add combinator to the last simple selector sequence
	 */
	public void attachCombinator(Combinator combinator){
		if(isEmpty()) throw new ParseException("Cannot have combinator " + 
				"prior to the first sequence of simple selectors.");
		getLastSequence().setCombinator(combinator);
	}
	
	/**
	 * Return the index of this selector in a multiple selector sequence.
	 */
	public int getSelectorIndex(){
		return _selectorIndex;
	}
	
	public SelSequence getLastSequence(){
		return(isEmpty())? 
				null : get(size()-1);
	}
	
	/**
	 * Return the i-th combinator
	 */
	public Combinator getCombinator(int index){
		return get(index).getCombinator();
	}
	
	/**
	 * 
	 * @param seqIdx
	 * @param selector
	 * @return
	 * 
	 */
	public static SelSequence getIfAny(int seqIdx, Selector selector) {
		return (seqIdx >= 0) ? selector.get(seqIdx) : null;
	}
	/**
	 * 
	 * @param seqIdx
	 * @return
	 * @throws IndexOutOfBoundsException when seqIdx is equals or greater than selector size.
	 */
	public SelSequence getIfAny(int seqIdx) {
		return getIfAny(seqIdx, this);
	}
	/**
	 * The model of selector is like this:<br>
	 * 
	 * <pre>
	 * SEQ->CB->SEQ->CB->SEQ
	 * </pre>
	 * <p>
	 * when there's a match and seqQualifidIdx is moving from 1 SEQ to another,
	 * the new state need to know how it has been calculated and depends on what
	 * kind of combinator.
	 * </p>
	 * Represent the Combinator of selector.
	 * 
	 * @author simonpai, Ian YT Tsai
	 */
	public enum Combinator {
		// <- failed success ->
		DESCENDANT(" ", false), 
		CHILD(" > ", false), 
		ADJACENT_SIBLING(" + ", true), // NEXT_SIBLING
		GENERAL_SIBLING(" ~ ", true);// REST_SIBLING

		private final String _str;
		private final boolean _backwardWhileFailed;

		Combinator(String str, boolean backwardWhileFailed) {
			_str = str;
			_backwardWhileFailed = backwardWhileFailed;
		}

		@Override
		public String toString() {
			return _str;
		}
		
		public boolean shouldBackward(){
			return _backwardWhileFailed;
		}
		
	}
	
	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int size = size();
		for(int i=0; i<size; i++){
			SelSequence seq = get(i);
			sb.append(seq);
			if(i < size-1) sb.append(seq.getCombinator());
		}
		return sb.toString();
	}

	public SelSequence appendNewSequnce() {
		SelSequence newSeq = new SelSequence(getLastSequence());
		this.add(newSeq);
		return newSeq;
	}

	
	public void finish() {
		this.getLastSequence();
	}
	
	
	
}
