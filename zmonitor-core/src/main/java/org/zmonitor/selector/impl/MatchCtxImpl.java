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
public class MatchCtxImpl implements MatchCtx, MatchCtxCtrl{

	private MatchCtx _parent;
	private Entry _comp;
	
	// qualified positions
	private boolean[][] _qualified;
	
	// pseudo-class support
	private int _compChildIndex = -1;
	
	public boolean[][] getQualified(){
		return _qualified;
	}
	
	/*package*/ MatchCtxImpl(Entry component, List<Selector> selectorList){
		_comp = component;
		_qualified = new boolean[selectorList.size()][];
		
		for(Selector selector : selectorList)
			_qualified[selector.getSelectorIndex()] = new boolean[selector.size()];
		
		_compChildIndex = _comp.getIndex();
	}
	
	/*package*/ MatchCtxImpl(Entry component, MatchCtx parent){
		_comp = component;
		
		MatchCtxCtrl ctrl = (MatchCtxCtrl) parent;
		boolean[][] parentQualified = ctrl.getQualified();
		
		int selectorListSize = parentQualified.length;
		_qualified = new boolean[selectorListSize][];
		for(int i=0; i < selectorListSize; i++){
			_qualified[i] = new boolean[ parentQualified[i].length];
		}
		_parent = parent;
		_compChildIndex = 0;
	}
	
	
	// operation //
	public void moveToNextSibling(){
		_comp = _comp.getNextSibling();
		_compChildIndex++;
	}
	
	
	
	// getter //
	/**
	 * Return the parent context
	 */
	public MatchCtx getParent(){
		return _parent;
	}
	
	/**
	 * Return the component.
	 */
	public Entry getEntry(){
		return _comp;
	}
	
	/**
	 * Return the child index of the component. If the component is one of the 
	 * page roots, return -1.
	 */
	public int getChildIndex(){
		if(_compChildIndex > -1) return _compChildIndex;
		Entry parent = _comp.getParent();
		return parent == null ? -1 : _comp.getIndex();
	}
	
	/**
	 * Return the count of total siblings of the component, including itself.
	 * @return
	 */
	public int getSiblingSize(){
		Entry parent = _comp.getParent();
		return parent == null ? 
				_comp.getEntryContainer().size() : 
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
		return _qualified[selectorIndex][position];
	}
	
	public void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}
	
	public void setQualified(int selectorIndex, int position, 
			boolean qualified) {
		_qualified[selectorIndex][position] = qualified;
	}
	
	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 * @return
	 */
	public boolean isMatched() {
		for(int i = 0; i< _qualified.length; i++) 
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
		boolean[] quals = _qualified[selectorIndex];
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
		for(boolean[] bs : _qualified) { 
			sb.append("Q[");
			for(boolean b : bs) sb.append(b?'1':'0');
			sb.append("]");
		}
		return sb.append(", ").append(_comp).toString();
	}
	
	
	
	// helper //
	private static int getComponentIndex(Entry curr){
		int index = -1;
		while(curr != null) {
			curr = curr.getPreviousSibling();
			index++;
		}
		return index;
	}

}
