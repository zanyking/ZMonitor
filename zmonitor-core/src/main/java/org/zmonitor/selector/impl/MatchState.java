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
	
	private final SelSequence lvSeq;
	private SelSequence inheritedSeq;
	private final Selector selector;
	
	private MatchState(
			SelSequence lvSeq,
			SelSequence inheritedSeq, 
			Selector selector) {
		this.lvSeq = lvSeq;
		this.inheritedSeq = inheritedSeq;
		this.selector = selector;
	}
	
	

	public boolean isMatched(){
		if(lvSeq==null)return false;
		return lvSeq.getNext()==null;
	}


	public String toString(){
		int lvSeqIdx = lvSeq==null? -1 : lvSeq.getIndex();
		int inheritedSeqIdx = inheritedSeq==null? -1 : inheritedSeq.getIndex();
		
		return "["+lvSeqIdx+":"+inheritedSeqIdx+"]";
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
		SelSequence rootSeq = 
				isMatches ? firstSeq : null;
		
		SelSequence inheritedSeq = 
				Direction.INHERIT.isCbInSameDirection(rootSeq)?
				rootSeq : null; 
		
		MatchState rootMCtx = new MatchState( rootSeq, 
				inheritedSeq, 
				selector);
		
		return rootMCtx;
	}
	/**
	 *   CHILD - next level only
	 *   DECENDENT - rest level
	 * 
	 * @param lvSeq
	 * @return
	 */
	public<E> MatchState toFirstChild(
			SelectorContext<E> newCtx){
		
		Result res = forward(Direction.INHERIT, inheritedSeq, newCtx, selector);
		SelSequence firstChildPresentSeq;
		SelSequence firstChildInheritableSeq;
		if(res.isForward){
			firstChildPresentSeq = res.nextSeq;
			firstChildInheritableSeq =
				selector.getIfAny( res.nextSeq.getInheritableIdx()); 
		}else{
			firstChildPresentSeq = inheritedSeq;
			firstChildInheritableSeq = inheritedSeq ==null? 
				inheritedSeq : selector.getIfAny(inheritedSeq.getInheritableIdx());
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

		SelSequence parentInheritedSeq = 
				newCtx.getParent().getMatchState(selector.getSelectorIndex()).inheritedSeq;
		
		SelSequence nextSibPresentSeq = null;
		SelSequence nextSibInheritableSeq = null;
		Result res;
		SelSequence targetSeq = SelSequence.reachedEnd(lvSeq)?
				Direction.SIBLING.backward(lvSeq, selector) : lvSeq;
		
		if(Direction.SIBLING.hasNext(targetSeq)){// use lvSeq
			res = forward(Direction.SIBLING, targetSeq, newCtx, selector);
		}else{// from parent direction to bean...
			res = forward(Direction.INHERIT, parentInheritedSeq, newCtx, selector);
		}
		nextSibPresentSeq =
				SelSequence.greaterThan(res.nextSeq, parentInheritedSeq)?
					res.nextSeq : parentInheritedSeq;
		
		if(res.isForward){
			nextSibInheritableSeq =nextSibPresentSeq==null? null:
				selector.getIfAny( nextSibPresentSeq.getInheritableIdx()); 
		}else{
			nextSibInheritableSeq =parentInheritedSeq==null? null:
				selector.getIfAny( parentInheritedSeq.getInheritableIdx());
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
	
	final boolean isForward;
	public Result(SelSequence nextSeq, boolean isForward) {
		super();
		this.nextSeq = nextSeq;
		this.isForward = isForward;
	}
	
}

