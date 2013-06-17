/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;
import org.zmonitor.selector.impl.model.SequenceMatcher;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.Strings;

/**
 * 
 * 
 * immutable object for state transition. <br>
 * is designed to record the state while traversing the entire entry tree.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorContext<E> implements SequenceMatcher, MatchCtx<E>{
	
	
	private final List<Selector> selectors;
	private final Map<String, PseudoClassDef> defs;
	private final SelectorContext<E> parent;
	
	private final Entry<E> entry;
	
	private final int[] qualifiedSeqIdxArray;	
	
	
	@Override
	public String toString() {
		return Arrays.toString(qualifiedSeqIdxArray) +
				", entry=" + entry ;
	}

	/**
	 * 
	 * @param selectors
	 * @param defs
	 * @param entry
	 */
	private SelectorContext(List<Selector> selectors, 
			Map<String, PseudoClassDef> defs, 
			Entry<E> entry) {//Root
		Arguments.checkNotNull(entry);
		Arguments.checkNotNull(selectors);
		Arguments.checkNotNull(defs);
		
		this.entry = entry;
		
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
		Arguments.checkNotNull(entry);
		this.entry = entry;
		this.parent = parent;
		this.previousSibling = previousSibling;
		this.selectors = parent.selectors;
		this.defs = parent.defs;
		this.qualifiedSeqIdxArray = qualifiedSeqIdxArray;
	}

	
	public SelectorContext<E> getParent() {
		return parent;
	}
	
	private final SelectorContext<E> previousSibling;//for debug only.
	private SelectorContext<E> firstChild;// for debug only.
	private SelectorContext<E> nextSibling;// for debug only.

	public SelectorContext<E> getFirstChild() {
		return firstChild;
	}

	public SelectorContext<E> getNextSibling() {
		return nextSibling;
	}
	public SelectorContext<E> getPreviousSibling() {
		return previousSibling;
	}

	/**
	 * TO_ROOT:
	 * parentCtx is null, preSiblingCtx is null.
	 * 
	 * @param entry
	 * @param selectors
	 * @param defs
	 * @return
	 */
	public static<E> SelectorContext<E> toRoot(Entry<E> entry, 
			List<Selector> selectors, 
			Map<String, PseudoClassDef> defs){
		SelectorContext<E> rootCtx = new SelectorContext<E>(
				selectors, defs, entry);
		//TODO apply selector...
		int selIdx = -1;
		int seqIdx = -1;
		for (Selector selector : selectors) {
			selIdx = selector.getSelectorIndex();
			//perform matching and retrieve new seqIdx.
			rootCtx.updateQualifiedIdx(selIdx, seqIdx, selector);
		}
		return rootCtx;
	}
	
	
	
	/**
	 * 
	 * TO_FIRST_CHILD: 
	 * 	parentCtx has value, preSiblingCtx is null.
	 * @return
	 */
	public SelectorContext<E> toFirstChild(){// this is parent
		
		Entry<E> firstChildEntry = entry.getFirstChild();
		if(firstChildEntry==null)
			return null;// this is leaf...

		int[] childQSIArray = new int[this.qualifiedSeqIdxArray.length];
		for(int i=0;i<childQSIArray.length;i++){
			childQSIArray[i] = -1;
		}
		
		int selIdx = -1;
		int seqIdx = -1;
		// for each Selector
		SelectorContext<E> childCtx = new SelectorContext<E>(
				firstChildEntry, childQSIArray, this, null);
		
		for (Selector selector : selectors) {
			selIdx = selector.getSelectorIndex();
			
			//retrieve initial seqIdx of this node.
			seqIdx = initChildSeqIdx(qualifiedSeqIdxArray[selIdx], 
					selector);
			
			//perform matching and retrieve new seqIdx.
			childCtx.updateQualifiedIdx(selIdx, seqIdx, selector);
		}
		
		return this.firstChild = childCtx;
	}
	
	/**
	 * get a proper initial Idx for first child.
	 * <pre>
	 * node
	 *  |-node
	 *  |-node		PARENT
	 *      |- node	CURRENT
	 * <pre>
	 * @param parentSeqIdx
	 * @param selector
	 * @return
	 */
	private static int initChildSeqIdx(final int parentSeqIdx, Selector selector){
		
		SimpleSelectorSequence parentSeq = selector.getIfAny(parentSeqIdx);
		
		//has no match, or already matched.
		if(parentSeq==null || parentSeq.getNext()==null) 
			return -1;
		else
			return parentSeq.getIndex();
	}
	
	
	/**
	 * TO_NEXT_SIBLING:
	 *  parentCtx has value, preSiblingCtx has value.
	 * 
	 * @return
	 */
	public SelectorContext<E> toNextSibling(){// this is previous sibling
		Entry<E> nextSiblingEntry = entry.getNextSibling();
		if(nextSiblingEntry==null)
			return null;

		int[] pArr = parent.qualifiedSeqIdxArray;
		int[] sArr = this.qualifiedSeqIdxArray;
		
		int[] childQSIArray = Arrays.copyOf(pArr, pArr.length); 
		
		SelectorContext<E> childCtx = new SelectorContext<E>(
				nextSiblingEntry, childQSIArray, parent, this);
		
		int selIdx = -1;
		SimpleSelectorSequence s_seq;
		
		for (Selector selector : selectors) {// for each Selector
			selIdx = selector.getSelectorIndex();
			s_seq = selector.getIfAny(sArr[selIdx]);
			
			int currentSeqIdx = -1;
			boolean isNextCBAboutSibling = isSeqCbIn(s_seq, 
					Combinator.ADJACENT_SIBLING, 
					Combinator.GENERAL_SIBLING);
			
			System.out.println("isNextCBAboutSibling="+isNextCBAboutSibling+", seq:"+
					SimpleSelectorSequence.toStringWithCB(s_seq) );
			
			if(s_seq!=null && isNextCBAboutSibling){
				currentSeqIdx = s_seq.getIndex();	
			}else{
				currentSeqIdx = initChildSeqIdx(pArr[selIdx], selector);
			}

			childCtx.updateQualifiedIdx(selIdx, currentSeqIdx, selector);
		}
		
		return nextSibling = childCtx;
		
	}
	
	private static boolean isSeqCbIn(SimpleSelectorSequence seq, Combinator...cbs){
		if(seq==null)return false;
		for(Combinator cb : cbs){
			if(seq.getCombinator()==cb)return true;
		}
		return false;
	}
	
	private void updateQualifiedIdx(int selIdx, int seqQualifiedIdx, Selector selector) {
		int candidateIdx = selector.matches(seqQualifiedIdx, this);
		qualifiedSeqIdxArray[selIdx] = candidateIdx;
	}


	public boolean matches(SimpleSelectorSequence sequence) {
		return EntryLocalProperties.match(entry, sequence, defs);
	}

	public boolean isMatched() {
		for(int i=0, j=qualifiedSeqIdxArray.length;i<j;i++){
			if(qualifiedSeqIdxArray[i]==selectors.get(i).size()-1)
				return true;
		}
		return false;
	}

	public Entry<E> getEntry() {
		return entry;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static void printTree(SelectorContext current, String indent){
		
		StringBuffer sb = new StringBuffer();
		int lv = 0;
		
		SelectorContext next = current;
		
		AD:
		while(next!=null){//DFS
			Strings.appendln(sb, toIndent(lv, indent), next);
			
			if(next.getFirstChild()!=null){//search child first
				next = next.getFirstChild();
				lv++;
			}else{//no child, search next sibling.
				while(next.getNextSibling()==null){
					next = next.getParent();
					lv--;
					if(next==null || next==current)
						break AD;
				}
				next = next.getNextSibling();
			}
		}
		
		System.out.println("---------------------------------");
		System.out.println(sb);
		System.out.println("---------------------------------");
	}
	
	private static String toIndent(int lv, String indent){
		if(lv==0)return "";
		StringBuffer sb = new StringBuffer();
		for(int i = 1;i<=lv;i++){
			sb.append(indent);
		}
		return sb.toString();
	}
	

}
