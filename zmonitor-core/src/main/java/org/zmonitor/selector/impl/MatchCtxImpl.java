/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.EntryLocalProperties;
import org.zmonitor.selector.impl.MatchCtx;
import org.zmonitor.selector.impl.MatchCtxCtrl;
import org.zmonitor.selector.impl.PseudoClassDef;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;


/**
 * A wrapper of Component, providing a context for selector matching algorithm.
 * @since 6.0.0
 * @author simonpai
 */
public class MatchCtxImpl<E> implements MatchCtx<E>, MatchCtxCtrl {
	
	private MatchCtx<E> _parent;
	private Entry<E> _comp;
	
	// qualified positions
	private final boolean[][] _qualified;
	
	// pseudo-class support
	private int _compChildIndex = -1;
	
	
	
	/*package*/ MatchCtxImpl(Entry<E> component) { // used by root jumping
		_comp = component;
		_qualified = new boolean[0][0];
		_compChildIndex = component.getIndex();
	}
	
	/*package*/ MatchCtxImpl(Entry<E> component, List<Selector> selectorList) { // root
		_comp = component;
		_qualified = new boolean[selectorList.size()][];
		
		for(Selector selector : selectorList)
			_qualified[selector.getSelectorIndex()] = new boolean[selector.size()];
		
		_compChildIndex = component.getIndex();
	}
	
	/*package*/ MatchCtxImpl(Entry<E> component, MatchCtx<E> parent) {// first child
		_comp = component;
		boolean[][] parentQualified = ((MatchCtxCtrl)parent).getQualified();
		
		int selectorListSize = parentQualified.length;
		_qualified = new boolean[selectorListSize][];
		int pSeqQLength;// parent Sequence qualified array length
		for (int i = 0; i < selectorListSize; i++) {
			pSeqQLength = parentQualified[i].length;
			_qualified[i] = Arrays.copyOf(parentQualified[i], pSeqQLength);
//			_qualified[i][pSeqQLength - 1] = false;// last one should always be
													// false, it's the element
													// itself should check
		}

		_parent = parent;
		_compChildIndex = 0;
	}
	
	
	
	// operation //
	public void moveToNextSibling() {
		_comp = _comp.getNextSibling();
		_compChildIndex++;
	}
	
	
	
	// getter //
	/**
	 * Return the parent context
	 */
	public MatchCtx<E> getParent() {
		return _parent;
	}
	
	/**
	 * Return the component.
	 */
	public Entry<E> getEntry() {
		return _comp;
	}
	
	/**
	 * Return the child index of the component. If the component is one of the 
	 * page roots, return -1.
	 */
	public int getChildIndex() {
		if(_compChildIndex > -1) return _compChildIndex;
		Entry<E> parent = _comp.getParent();
		return parent == null ? -1 : _comp.getIndex();
	}
	
	/**
	 * Return the count of total siblings of the component, including itself.
	 */
	public int getSiblingSize() {
		Entry parent = _comp.getParent();
		return parent == null ? 
				_comp.getEntryContainer().size() : 
					parent.size();
	}
	
	
	
	// match position //
	/**
	 * Return true if the component matched the given position of the given 
	 * selector.
	 * @param selectorIndex
	 * @param position
	 */
	public boolean isQualified(int selectorIndex, int position) {
		if (selectorIndex < 0 || selectorIndex >= _qualified.length)
			return false;
		boolean[] posq = _qualified[selectorIndex];
		return position > -1 && position < posq.length && posq[position];
	}
	
	public void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}
	
	public void setQualified(int selectorIndex, int position, 
			boolean qualified) {
		_qualified[selectorIndex][position] = qualified;
	}
	public boolean[][] getQualified(){
		return _qualified;
	}
	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 */
	public boolean isMatched() {
		for (int i = 0; i < _qualified.length; i++) 
			if (isMatched(i)) 
				return true;
		return false;
	}
	
	/**
	 * Return true if the component matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 */
	public boolean isMatched(int selectorIndex) {
		if (selectorIndex < 0 || selectorIndex >= _qualified.length)
			return false;
		boolean[] quals = _qualified[selectorIndex];
		return quals[quals.length - 1];
	}
	
	
	
	// match local property //
	/**
	 * Return true if the component qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 */
	public boolean match(SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs) {
		boolean matches = EntryLocalProperties.match(this, seq, defs);
		return matches;
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		str(sb, _qualified);
		sb.append(' ');
		for (MatchCtx<E> c = this; (c = c.getParent()) != null;)
			sb.append("  ");
		sb.append(_comp);
		return sb.toString();
	}
	
	private static void str(StringBuilder sb, boolean[][] arr) {
		if (arr.length > 1)
			sb.append('[');
		for (int i = 0; i < arr.length; i++) {
			if (i > 0)
				sb.append(',');
			str(sb, arr[i]);
		}
		if (arr.length > 1)
			sb.append(']');
	}
	
	private static void str(StringBuilder sb, boolean[] arr) {
		sb.append('[');
		for (boolean b : arr)
			sb.append(b ? '1' : '0');
		sb.append(']');
	}

	
	
}
