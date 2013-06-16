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
//	/**

//	 * 
//	 * @author Ian YT Tsai(Zanyking)
//	 * 
//	 */
//	public interface Transition {
//		/**
//		 * 
//		 * @param seqMatcher
//		 * @param seq
//		 * @return
//		 */
//		SimpleSelectorSequence transit(SequenceMatcher seqMatcher,
//				SimpleSelectorSequence seq);
//	}
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
	 * 
	 * <pre>
	 * 
	 * SUCCESS:
	 * 
	 * 	given: <div clz="a b c">
	 * 	sel [-1]:            .a      .b      .c 
	 * 	transition: [-1] -> [ 0] -> [ 1] -> [ 2]
	 *  sel [-1]:            .a   >  .b      .c 
	 * 	transition: [-1] -> [ 0]
	 * 
	 * 	given: <div clz="a b c">
	 * 	sel [-1]:            .a      .b   >  .c 
	 * 	transition: [-1] -> [ 0] -> [ 1]    [  ]
	 * 
	 * 	given: <div clz="a b c">
	 *  sel [-1]:            .a      .b   +  .c 
	 * 	transition: [-1] -> [ 0] -> [ 1]    [  ]
	 * 
	 * 	given: <div clz="a b c"> 
	 * 	sel [-1]:            .a      .b   ~  .c
	 * 	transition: [-1] -> [ 0] -> [ 1]    [  ]
	 * 
	 *  
	 *  FAILED:
	 *  
	 *  given: <div clz="c">
	 * 	sel [ 1]:            .x      .y      .z 
	 * 	transition: [-1]    [ 0]    [ 1]    [  ]
	 *  sel [ 0]:            .z      .b      .a 
	 * 	transition: [  ]    [ 0] -> [ 1] -> [ 2]
	 * 
	 * 
	 *  given: <div clz="c">
	 * 	sel [ 1]:            .x   +  .y   +  .z 
	 * 	transition: [  ]    [ 0] <- [ 1] <- [ X]
	 *                |  <-   |  <-   |
	 * 
	 * 	sel [ 1]:            .x      .y   +  .z 
	 * 	transition: [  ]    [  ]    [ 1] <- [ X]
	 * 
	 * </pre>
	 * 
	 * Represent the Combinator of selector.
	 * 
	 * @author simonpai, Ian YT Tsai
	 */
	public enum Combinator {
		// <- failed success ->
		DESCENDANT(" ", false, true), 
		CHILD(" > ", false, false), 
		ADJACENT_SIBLING(" + ", true, false), // NEXT_SIBLING
		GENERAL_SIBLING(" ~ ", true, false);// REST_SIBLING

		private final String _str;
		private final boolean _forwardWhileSuccess;
		private final boolean _backwardWhileFailed;

		Combinator(String str, boolean backwardWhileFailed,
				boolean forwardWhileSuccess) {
			_str = str;
			_forwardWhileSuccess = forwardWhileSuccess;
			_backwardWhileFailed = backwardWhileFailed;
		}

		@Override
		public String toString() {
			return _str;
		}

		/**
		 * 
		 * @param seqMatcher
		 * @param startSeq 
		 * 	the seq which start from the next of current qualified to the end.
		 * 
		 * @return the current qualified sequence index. 
		 */
		public int matches(SequenceMatcher seqMatcher,
				SimpleSelectorSequence startSeq) {

			int presentQualifiedSeqIdx = startSeq.getIndex()-1;

			SimpleSelectorSequence chainedSeq;
			
			boolean isMatch = seqMatcher.matches(startSeq);
			
			if (isMatch) {
				presentQualifiedSeqIdx++;
				chainedSeq = startSeq.getNext();
				if(chainedSeq!=null)
					presentQualifiedSeqIdx = 
						forward(presentQualifiedSeqIdx, 
								chainedSeq, seqMatcher);
				
			} else {// failed.
				presentQualifiedSeqIdx = 
					backward(presentQualifiedSeqIdx, 
							startSeq.getPrevious());//present seq
			}
			return presentQualifiedSeqIdx;
		}
		
		
		private int forward(int idx, SimpleSelectorSequence seq, 
				SequenceMatcher seqMatcher){
			
			boolean forward = shouldForward(seq);
			
			while(forward){
				if(!seqMatcher.matches(seq)){//failed, stop here.
					forward = false;
				}
				idx++;
				seq = seq.getNext();
				forward = (seq==null) ?// test if reach the selector end. 
					false : shouldForward(seq);
			}
			return idx;
		}
		private boolean shouldForward(SimpleSelectorSequence seq){
			if(seq==null)return false;
			return seq.getCombinator()._forwardWhileSuccess;
		}
		private int backward(int idx, SimpleSelectorSequence seq){
			boolean backward = shouldBackward(seq);
			while(backward){
				idx--;
				seq = seq.getPrevious();
				backward = (seq==null) ?// test if reach the selector head. 
						false : shouldBackward(seq);
			}
			return idx;
		}
		private boolean shouldBackward(SimpleSelectorSequence seq){
			if(seq==null)return false;
			return seq.getCombinator()._backwardWhileFailed;
		}
		
	}
	
	
	/**
	 * <pre>
	 * PresentQualifiedSeqIdx
	 *       |         
	 *       |
	 *      -1          0            1            2    
	 *                 SEQ -> CB -> SEQ -> CB -> SEQ
	 *       FAIELD <-  |  -> SUCCESS
	 *                  |
	 *              SeqMatcher
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
	 *      SEQ -> CB -> SEQ -> CB -> SEQ
	 *         FAIELD <-  |  -> SUCCESS
	 *                    |
	 *                SeqMatcher
	 *                
	 * </pre>
	 * @param seqenceIdx
	 * @param seqMatcher
	 * @return
	 */
	public int matches(int presentQualifiedSeqIdx, SequenceMatcher seqMatcher){
		int size = this.size();
		
		Arguments.checkInterval(presentQualifiedSeqIdx, -1, size-1);
		
		// look up next seq to test current matcher.
		int nextSeqIdx = presentQualifiedSeqIdx+1;
		
		if (nextSeqIdx == size) {// reach the end, no next matcher.
			return presentQualifiedSeqIdx;
			// TODO is this good? currently selector knows nothing about
			// tree structure, so I think this is the best response.
		} else {
			SimpleSelectorSequence nextSeq = get(nextSeqIdx);
			return nextSeq.getCombinator().matches(seqMatcher, nextSeq);
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
		return new SimpleSelectorSequence(getLastSequence());
	}
	
}
