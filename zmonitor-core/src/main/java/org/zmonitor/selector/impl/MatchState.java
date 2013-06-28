/**
 * 
 */
package org.zmonitor.selector.impl;

import org.zmonitor.selector.impl.model.Direction;
import org.zmonitor.selector.impl.model.SelSequence;
import org.zmonitor.selector.impl.model.Selector;

/**
 * <pre>
 * PresentQualifiedSeqIdx
 *       |         
 *       |
 *  -1   0            1            2    
 *      SEQ -> CB -> SEQ -> CB -> SEQ -> CB
 *         FAIELD <-  |            |
 *                    |            |
 *                SeqMatcher      target
 * </pre>
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class MatchState {
	
	private final SelSequence presentSeq;
	private SelSequence inheritableSeq;
	private final Selector selector;
	
	private MatchState(
			SelSequence lvSeq,
			SelSequence inheritedSeq, 
			Selector selector) {
		this.presentSeq = lvSeq;
		this.inheritableSeq = inheritedSeq;
		this.selector = selector;
	}
	
	

	public boolean isMatched(){
		if(presentSeq==null)return false;
		return SelSequence.reachedEnd(presentSeq);
	}


	public String toString(){
		int pSeqIdx = presentSeq==null? -1 : presentSeq.getIndex();
		int inheritedSeqIdx = inheritableSeq==null? -1 : inheritableSeq.getIndex();
		
		return "["+pSeqIdx+":"+inheritedSeqIdx+"]";
	}

	/**
	 * 
	 * @param selCtx
	 * @param selector
	 * @return
	 */
	public static <E> MatchState toRoot(SelectorContext<E> selCtx, Selector selector){
		
		SelSequence firstSeq = selector.get(0);
		boolean isMatches = selCtx.isEntryMatches(firstSeq); 
		
		SelSequence rootPresentSeq = 
				isMatches ? firstSeq : null;
		
		SelSequence inheritedSeq = 
				Direction.INHERIT.isCbInSameDirection(rootPresentSeq)?
				!SelSequence.reachedEnd(rootPresentSeq)?		
				rootPresentSeq : null : null; 
		
		MatchState rootMCtx = new MatchState( rootPresentSeq, 
				inheritedSeq, 
				selector);
		
		return rootMCtx;
	}
	/**
	 *   CHILD - next level only
	 *   DECENDENT - rest level
	 * 
	 * @param presentSeq
	 * @return
	 */
	public<E> MatchState toFirstChild(
			SelectorContext<E> newCtx){
		
		Result res = forward(Direction.INHERIT, inheritableSeq, newCtx, selector);
		
		
		SelSequence firstChildPresentSeq = res.isForwarded ? 
			res.nextSeq : // got a match.  
			inheritableSeq; // at least to be inherited
		
		SelSequence firstChildInheritableSeq = null;
		if(firstChildPresentSeq !=null){
			firstChildInheritableSeq = 
				(Direction.INHERIT.matchNext(firstChildPresentSeq))?
				firstChildPresentSeq :
				selector.getIfAny(firstChildPresentSeq.getInheritableIdx());
		}
		
		return new MatchState( firstChildPresentSeq, firstChildInheritableSeq , selector);
	}
	/**
	 *   NEXT_SIBLING - next sibling only
	 *   REST_SIBLING - rest sibling
	 *    
	 * @param siblingSeq
	 * @return
	 */
	public<E> MatchState toNextSibling(SelectorContext<E> newCtx){

		SelSequence parentInheritableSeq = 
				newCtx.getParent().getMatchState(
						selector.getSelectorIndex()).inheritableSeq;
		
		
		Result res;
		SelSequence targetSeq = SelSequence.reachedEnd(presentSeq)?
				Direction.SIBLING.backward(presentSeq, selector) : 
				presentSeq;
		
		if(Direction.SIBLING.hasNext(targetSeq)){// use previous sibling's present Seq first.
			res = forward(Direction.SIBLING, targetSeq, newCtx, selector);
		}else{// from parent direction to bean...
			res = forward(Direction.INHERIT, parentInheritableSeq, newCtx, selector);
		}
		SelSequence nextSibPresentSeq =
				SelSequence.greaterThan(res.nextSeq, parentInheritableSeq)?
					res.nextSeq : parentInheritableSeq;

		SelSequence nextSibInheritableSeq = null;		
		if(nextSibPresentSeq !=null){
			nextSibInheritableSeq = 
				(Direction.INHERIT.matchNext(nextSibPresentSeq))?
						nextSibPresentSeq :
				selector.getIfAny(nextSibPresentSeq.getInheritableIdx());
		}
		
		
		MatchState mState = new MatchState( nextSibPresentSeq, nextSibInheritableSeq , selector);
		return mState;
	}
	/**
	 * find next, see is able to forward, if not, rollback
	 * @param seq
	 * @param newCtx
	 * @param selector
	 * @return
	 */
	public static <E> Result forward(Direction dir, SelSequence seq, SelectorContext<E> newCtx, Selector selector){
		SelSequence nextSeq = dir.getNext(seq, selector);
		if(dir.hasNext(seq) && newCtx.isEntryMatches(nextSeq)){
			return new Result(nextSeq, true);
		}
		return new Result(dir.backward(seq, selector), false); 
	}
	
	
}

class Result{
	final SelSequence nextSeq;
	
	final boolean isForwarded;
	public Result(SelSequence nextSeq, boolean isForward) {
		super();
		this.nextSeq = nextSeq;
		this.isForwarded = isForward;
	}
	
}

