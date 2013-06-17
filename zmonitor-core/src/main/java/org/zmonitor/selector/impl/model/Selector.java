/**
 * 
 */
package org.zmonitor.selector.impl.model;

import java.util.ArrayList;

import org.zmonitor.selector.impl.ParseException;
import org.zmonitor.util.Arguments;

/**
 * The model representing a selector.
 * @author simonpai
 */
public class Selector extends ArrayList<SimpleSelectorSequence> {
	
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
	
	public SimpleSelectorSequence getLastSequence(){
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
	public static SimpleSelectorSequence getIfAny(int seqIdx, Selector selector) {
		return (seqIdx >= 0) ? selector.get(seqIdx) : null;
	}
	/**
	 * 
	 * @param seqIdx
	 * @return
	 * @throws IndexOutOfBoundsException when seqIdx is equals or greater than selector size.
	 */
	public SimpleSelectorSequence getIfAny(int seqIdx) {
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

		
	}
	
	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int size = size();
		for(int i=0; i<size; i++){
			SimpleSelectorSequence seq = get(i);
			sb.append(seq);
			if(i < size-1) sb.append(seq.getCombinator());
		}
		return sb.toString();
	}

	public SimpleSelectorSequence appendNewSequnce() {
		SimpleSelectorSequence newSeq = new SimpleSelectorSequence(getLastSequence());
		this.add(newSeq);
		return newSeq;
	}
	/**
	 * <pre>
	 * PresentQualifiedSeqIdx
	 *       |         
	 *       |
	 *      -1          0            1            2    
	 *                 SEQ -> CB -> SEQ -> CB -> SEQ -> CB
	 *       FAIELD <-  |            |
	 *                  |            |
	 *              SeqMatcher      target
	 *                
	 * </pre>
	 * 
	 * 
	 * 
	 * 
	 * <pre>
	 * PresentQualifiedSeqIdx
	 *       |         
	 *       |
	 *  -1   0            1            2    
	 *      SEQ -> CB -> SEQ -> CB -> SEQ -> CB
	 *         FAIELD <-  |            |
	 *                    |            |
	 *                SeqMatcher      target
	 *                
	 * </pre>
	 * @param seqenceIdx
	 * @param seqMatcher
	 * @return
	 */
	public int matches(int inheritedSeqIdx, SequenceMatcher seqMatcher){
		int size = this.size();
		
		Arguments.checkInterval(inheritedSeqIdx, -1, size-1);
		
		// look up inherited to test current matcher.
		SimpleSelectorSequence inheritedSeq = this.getIfAny(inheritedSeqIdx);
		
		SimpleSelectorSequence nextSeq = inheritedSeq==null?
				get(0): inheritedSeq.getNext();
		
		if (nextSeq==null) {// reach the end, no next matcher.
			return inheritedSeqIdx;
			// TODO is this good? currently selector knows nothing about
			// tree structure, so I think this is the best response.
		} else {
			return matches0(seqMatcher, nextSeq, 
					inheritedSeq==null? null : inheritedSeq);
		}
	}
	
	private int matches0(SequenceMatcher seqMatcher,
			SimpleSelectorSequence startSeq,
			SimpleSelectorSequence backwardSeq) {
		
		boolean isMatch = seqMatcher.matches(startSeq);
		if (isMatch) {
			return startSeq.getIndex();
		} else {// failed, backward till reaching a Combinator which has
				// valid relationship to current node.
			return backward(backwardSeq);
		}
	}
	private int backward(SimpleSelectorSequence seq){
		boolean backward = shouldBackward(seq);
		while(backward){
			seq = seq.getPrevious();
			backward = (seq==null) ?// test if reach the selector head. 
					false : shouldBackward(seq);
		}
		return seq==null? -1: seq.getIndex();
	}
	private boolean shouldBackward(SimpleSelectorSequence seq){
		if(seq==null)return false;
		return seq.getCombinator()._backwardWhileFailed;
	}
	
	
}
