/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MatchCtxImpl<E> implements MatchCtx<E>, MatchCtxCtrl{

	private MatchCtx<E> parent;
	private Entry<E> entry;
	
	// qualified positions, a Dynamic Programming matrix to keep states.
	private boolean[][] qualifiedArr;
	
	// pseudo-class support
	private int entryIndex = -1;
	
	
	
	
	
	
	public boolean[][] getQualified(){
		return qualifiedArr;
	}
	
	/*package*/ MatchCtxImpl(Entry<E> entry, List<Selector> selectorList){
		this.entry = entry;
		this.qualifiedArr = new boolean[selectorList.size()][];
		
		for(Selector selector : selectorList)
			qualifiedArr[selector.getSelectorIndex()] = new boolean[selector.size()];
		
		this.entryIndex = this.entry.getIndex();
	}
	
	/*package*/ MatchCtxImpl(Entry<E> entry, MatchCtx<E> parent){
		this.entry = entry;
		
		boolean[][] parentQualified = 
				((MatchCtxCtrl) parent).getQualified();
		
		int selectorListSize = parentQualified.length;
		qualifiedArr = new boolean[selectorListSize][];
		for(int i=0; i < selectorListSize; i++){
			qualifiedArr[i] = new boolean[ parentQualified[i].length];
		}
		this.parent = parent;
		this.entryIndex = 0;
	}
	
	/*package*/ MatchCtxImpl(Entry<E> entry, boolean[][] qualifiedArr){
		this.entry = entry;
		this.qualifiedArr = qualifiedArr;
		this.entryIndex = this.entry.getIndex();
	}
	
	public MatchCtxImpl<E> toNext() {
		return new MatchCtxImpl<E>(entry.getNextSibling(), qualifiedArr);
	}
//	
//	// operation //
//	public void moveToNextSibling(){
//		entry = entry.getNextSibling();
//		entryIndex++;
//	}
	
	
	
	// getter //
	/**
	 * Return the parent context
	 */
	public MatchCtx<E> getParent(){
		return parent;
	}
	
	/**
	 * Return the component.
	 */
	public Entry<E> getEntry(){
		return entry;
	}
	
	/**
	 * Return the child index of the component. If the component is one of the 
	 * page roots, return -1.
	 */
	public int getChildIndex(){
		if(entryIndex > -1) return entryIndex;
		Entry<E> parent = entry.getParent();
		return parent == null ? -1 : entry.getIndex();
	}
	
	/**
	 * Return the count of total siblings of the component, including itself.
	 * @return
	 */
	public int getSiblingSize(){
		Entry<E> parent = entry.getParent();
		return parent == null ? 
				entry.getEntryContainer().size() : 
					parent.size();
	}
	
	// match position //
	/**
	 * Return true if the component matched the given position of the given 
	 * selector.
	 * @param selector
	 * @param position
	 * @return
	 */
	public boolean isQualified(int selectorIndex, int position) {
		return qualifiedArr[selectorIndex][position];
	}
	
	public void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}
	
	public void setQualified(int selectorIndex, int position, 
			boolean qualified) {
		qualifiedArr[selectorIndex][position] = qualified;
	}
	
	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 * @return
	 */
	public boolean isMatched() {
		for(int i = 0; i< qualifiedArr.length; i++) 
			if(isMatched(i)) 
				return true;
		return false;
	}
	
	/**
	 * Return true if the component matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 * @return
	 */
	public boolean isMatched(int selectorIndex) {
		boolean[] quals = qualifiedArr[selectorIndex];
		return quals[quals.length-1];
	}
	
	
	// match local property //
	/**
	 * Return true if the component qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 * @return
	 */
	public boolean match(SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs){
		
		return EntryLocalProperties.match(this, seq, defs);
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		for(boolean[] bs : qualifiedArr) { 
			sb.append("Q[");
			for(boolean b : bs) sb.append(b?'1':'0');
			sb.append("]");
		}
		return sb.append(", ").append(entry).toString();
	}

	
	
//	// helper //
//	private static int getComponentIndex(Entry curr){
//		int index = -1;
//		while(curr != null) {
//			curr = curr.getPreviousSibling();
//			index++;
//		}
//		return index;
//	}

}
