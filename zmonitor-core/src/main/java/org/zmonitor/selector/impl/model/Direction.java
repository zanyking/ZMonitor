/**
 * 
 */
package org.zmonitor.selector.impl.model;

import static org.zmonitor.selector.impl.model.Selector.Combinator.ADJACENT_SIBLING;
import static org.zmonitor.selector.impl.model.Selector.Combinator.CHILD;
import static org.zmonitor.selector.impl.model.Selector.Combinator.DESCENDANT;
import static org.zmonitor.selector.impl.model.Selector.Combinator.GENERAL_SIBLING;

import org.zmonitor.selector.impl.model.Selector.Combinator;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public enum Direction {
	INHERIT(CHILD, DESCENDANT) {// toFirstChild failed
		public <E> SelSequence backward(SelSequence seq,  Selector selector) {
			if(seq==null)return seq;
			return selector.getIfAny(seq.getInheritableIdx());
		}
	},
	SIBLING(ADJACENT_SIBLING, GENERAL_SIBLING) {// toNextSibling failed
		public <E> SelSequence backward(SelSequence seq, Selector selector) {
			if(seq==null)return seq;
			return selector.getIfAny(seq.getTransitableIdx());
		}
		
	};
	
	private Combinator nextCb;
	private Combinator restCb;
	Direction(Combinator nextCb, Combinator restCb){
		this.nextCb = nextCb;
		this.restCb = restCb;
	}
	
	public boolean isTransitive(Combinator cb){
		return restCb == cb;
	}
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public boolean isCbInSameDirection(SelSequence seq){
		if(seq==null)return true;
		Combinator cb = seq.getCombinator();
		return cb==restCb||cb==nextCb;
	}
	public boolean hasNext(SelSequence seq){
		if(seq==null)return true;
		if(seq.getNext()==null)return false;
		return isCbInSameDirection(seq);
	}
	public SelSequence getNext(SelSequence seq, Selector selector){
		if(seq==null)return selector.get(0);
		return seq.getNext();
	}
	/**
	 * 
	 * @param seq
	 * @param selector
	 * @return
	 */
	public abstract<E> SelSequence backward(SelSequence seq, Selector selector);
}
