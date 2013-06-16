/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;
import org.zmonitor.selector.impl.model.SequenceMatcher;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Arguments;

/**
 * 
 * 
 * immutable object for state transition. <br>
 * is designed to record the state while traversing the entire entry tree.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorContext<E> implements SequenceMatcher{
	
	
	private final List<Selector> selectors;
	private final Map<String, PseudoClassDef> defs;
	private final SelectorContext<E> parent;
	private final SelectorContext<E> previousSibling;
	private Entry<E> entry;
	
	private final int[] qualifiedSeqIdxArray;	
	
	/**
	 * 
	 * @param selectors
	 * @param defs
	 * @param entry
	 */
	private SelectorContext(List<Selector> selectors, 
			Map<String, PseudoClassDef> defs, 
			Entry<E> entry) {//Root

		Arguments.checkNotNull(selectors);
		Arguments.checkNotNull(defs);
		
		this.selectors = selectors;
		int selectorAmount = selectors.size();
		qualifiedSeqIdxArray = new int[selectorAmount];
		for(int i=0;i<qualifiedSeqIdxArray.length;i++){
			qualifiedSeqIdxArray[i] = -1;
		}
		this.defs = defs;
		this.parent = null;
		this.previousSibling = null;
	}
	
	private SelectorContext(Entry<E> entry, 
			int[] qualifiedSeqIdxArray,
			SelectorContext<E> parent,
			SelectorContext<E> previousSibling) {//others
		this.parent = parent;
		this.previousSibling = previousSibling;
		this.selectors = parent.selectors;
		this.defs = parent.defs;
		this.qualifiedSeqIdxArray = qualifiedSeqIdxArray;
	}
	
	/**
	 * TO_ROOT:
	 * parentCtx is null, preSiblingCtx is null.
	 * 
	 * TO_FIRST_CHILD: 
	 * 	parentCtx has value, preSiblingCtx is null.
	 * 
	 * TO_NEXT_SIBLING:
	 *  parentCtx has value, preSiblingCtx has value.
	 *  
	 * @param entry
	 * @param previousSiblingCtx
	 * @return
	 */

	
	public SelectorContext<E> toFirstChild(){// this is parent
		
		Entry<E> firstChild = entry.getFirstChild();
		if(firstChild==null)
			return null;
		
		int[] qualifiedSeqIdxArray = this.qualifiedSeqIdxArray;
		SelectorContext<E> parent = this;

		// TODO
		int selIdx = -1;
		// for each Selector
		for (Selector selector : selectors) {
			selIdx = selector.getSelectorIndex();
			int currentSeqIdx = qualifiedSeqIdxArray[selIdx];

			int newIdx = this.newQualifiedIdx(currentSeqIdx, selector);
			qualifiedSeqIdxArray[selIdx] = newIdx;
		}
		
		
		SelectorContext<E> childCtx = new SelectorContext<E>(
				firstChild, qualifiedSeqIdxArray, parent, null);
		
		return childCtx;
		
	}

	public SelectorContext<E> toNextSibling(){// this is previous sibling
		Entry<E> nextSibling = entry.getNextSibling();
		if(nextSibling==null)
			return null;
		
		int[] pArr = parent.qualifiedSeqIdxArray;
		int[] sArr = this.qualifiedSeqIdxArray;

		// TODO 
		int selIdx = -1;
		// for each Selector
		for (Selector selector : selectors) {
			
			selIdx = selector.getSelectorIndex();
			int currentSeqIdx = pArr[selIdx];

			int newIdx = this.newQualifiedIdx(currentSeqIdx, selector);
			pArr[selIdx] = newIdx;
		}
		
		
		SelectorContext<E> childCtx = new SelectorContext<E>(
				nextSibling, pArr, parent, this);
		
		return childCtx;
		
	}
	
	
	public static<E> SelectorContext<E> toRoot(Entry<E> entry, 
			List<Selector> selectors, 
			Map<String, PseudoClassDef> defs){
		SelectorContext<E> root = new SelectorContext<E>(
				selectors, defs, entry);
		return root;
	}
	
	
	
	
	private int newQualifiedIdx( int seqQualifiedIdx, Selector selector) {
		int candidateIdx = 0;
		if (seqQualifiedIdx > 0) {
			candidateIdx = seqQualifiedIdx;
		}
		candidateIdx = selector.matches(candidateIdx, this);
		// have to get state from parent or previous sibling.
		return candidateIdx;
	}


	public boolean matches(SimpleSelectorSequence sequence) {
		return EntryLocalProperties.match(entry, sequence, defs);
	}
	
	
	
	
	
	private SequenceMatcher getSequenceMatcher(Combinator cb){
		switch (cb) {
		/*
		 * DESCENDANT .a .b .c
		 *
		 * always the first combinator.
		 * TO_FIRST_CHILD
		 *  <div> [-1] -> [-1]
		 *    <div clz="a"> P[-1] -> [ 0]
		 *      <div clz="b"> P[ 0] -> [ 1]
		 *        <div> P[ 1]
		 *          <div> P[ 1]
		 *            <div clz="c"> P[ 1] -> [ 2]
		 *            	<div clz="x"> P[ 2]
		 *            	<div clz="y"> P[ 2]
		 *            	<div clz="z"> P[ 2]
		 *        <div clz="c"> P[ 1] -> [ 2]
		 *    <div clz="a b c"> P[-1] -> [ 0] -> [ 1] -> [ 2]
		 *        <div > P[ 2]
		 * 
		 * 
		 * TO_NEXT_SIBLING
		 *  <div> [-1]
		 *    <div clz="a"> P[-1] -> [ 0]
		 *    <div clz="c"> P[-1] -> [-1] 
		 *    <div clz="a"> P[-1] -> [ 0]
		 *    	<div clz="b c"> P[ 0] -> [ 1] -> [ 2]
		 * 
		 *  Both use the same strategy:
		 * 	inherit the state from parent.
		 * 		if(current Entry has matches to current seq)
		 *			seqQualifiedIdx++.
		 *		else 
		 *			do nothing.  
		 * 
		 */
		case DESCENDANT:
			
			//TODO
		/* 
		 * CHILD  .a > .b > .c
		 *
		 * TO_FIRST_CHILD
		 *   <div> [-1]
		 *    <div clz="a"> P[-1] -> [ 0]
		 *      <div> P[ 0] -> [-1]
		 *      <div clz="b"> P[0] -> [ 1]
		 *        <div clz="c"> P[ 1] -> [ 2]
		 *        <div > P[ 1] -> [ 0] -> [ -1]
		 *        <div> P[ 1] -> [ 0] [-1]
		 *          <div> p[-1]
		 *            <div clz="c"> p[-1]
		 *        
		 * TO_NEXT_SIBLING
		 *  <div> [-1]
		 *    <div clz="a"> P[-1] -> [ 0]
		 *      <div clz="c"> P[ 0] -> [-1] 
		 *      <div clz="b"> P[ 0] -> [ 1]
		 *    <div clz="c"> P[-1] -> [-1] 
		 *    <div clz="a"> P[-1] -> [ 0]
		 *    
		 * Both use the same strategy:
		 * 	
		 * inherit the state from parent.(not pre-sibling)
		 *	if(current Entry has matches to current seq)
		 *		seqQualifiedIdx++.
		 *	else 
		 *		seqQualifiedIdx = -1.  
		 * 
		 * 
		 */
		case CHILD:
			
			//TODO
			
			break;
		/* 
		 * ADJACENT_SIBLING 
		 * 
		 * TO_FIRST_CHILD .a  .b + .c
		 *   <div> [-1]
		 *    <div clz="a"> [ 0] first-child
		 *      <div> [ 0]
		 *      <div clz="b"> [ 1]
		 *        <div clz="c"> [ 0] first-child
		 *        <div> [ 1]
		 *          <div> [ 1]
		 *            <div clz="c"> [ 1]
		 *    <div clz="b"> [-1]
		 * 	
		 * inherit the state from parent: if parent's seq combinator is not ADJACENT_SIBLING
		 * start a new state: 
		 * there's no previous sibling.
		 * 	if(current Entry has matches to current seq)
		 *		seqQualifiedIdx++.
		 *	else 
		 *		seqQualifiedIdx--.  
		 *
		 * TO_FIRST_CHILD .a + .b + .c
		 *   <div> [-1]
		 *    <div clz="a"> p[-1] -> [ 0] first-child
		 *    <div clz="b"> S[ 0] -> [ 1]
		 *    <div clz="C"> S[ 1] -> [ 2]
		 *      <div> [-1] new child without a match has to deduct.
		 *      <div clz="a"> [ 0]
		 *      <div clz="b"> [ 1]
		 *        <div> [-1]
		 *          <div> [ 1]
		 *            <div clz="c"> [ 1]
		 *    
		 *    
		 *    
		 *            
		 * TO_NEXT_SIBLING .a + .b + .c
		 *   <div> [-1]
		 *    <div clz="a"> [ 0]
		 *    	  <div clz="a"> [ 0] fc
		 *        <div clz="b"> [ 1]
		 *        <div> [-1]  fc
		 *    <div clz="b"> [ 1]
		 *      <div clz="b"> [-1]  parent=1, preSibling=null, 
		 *        <div clz="a"> [ 0]  fc
		 *        <div clz="b"> [ 1]
		 *        <div> [-1]
		 *    <div clz="c"> [ 2]
		 *  1 match
		 * 
		 * 
		 * 
		 */
		case ADJACENT_SIBLING:
			
			//TODO
			
			break;
		/* 
		 * GENERAL_SIBLING .a .b ~ .c
		 *  very hard to satisfy, because to support this we have to do BFS rather then DFS to Entry Tree.
		 * 
		 * 
		 * TO_FIRST_CHILD
		 * TO_NEXT_SIBLING
		 * 
		 *  <div clz="a">[100]
		 * 	  <div clz="b">[110]
		 * 	    <div >[000]
		 * 	      <div clz="c">[000]
		 *        <div clz="a">[100]
		 *          <div clz="b">[110]
		 *      <div clz="c">[111]
		 *  1 match
		 * 
		 * 
		 * 
		 */		
		case GENERAL_SIBLING:
			
			//TODO
			
			break;
		}
		throw new IllegalArgumentException();
	}
	


}
