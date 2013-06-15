/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;
import org.zmonitor.selector.impl.EntryLocalProperties;
import org.zmonitor.selector.impl.MatchCtx;
import org.zmonitor.selector.impl.MatchCtxCtrl;
import org.zmonitor.selector.impl.PseudoClassDef;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.Strings;

/**
 * An implementation of Iterator&lt;Component> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * @since 6.0.0
 * @author simonpai
 */
public class EntryIterator<E> implements Iterator<Entry<E>> {
	
	private final EntryContainer<E> _container;
	private final Entry<E> _root;
	private final List<Selector> _selectorList;
	private final int _posOffset;
	private final boolean _allIds;
	private final Map<String, PseudoClassDef> _localDefs = 
		new HashMap<String, PseudoClassDef>();
	
	private Entry<E> _offsetRoot;
	private MatchCtx<E> _currCtx;
	
	/**
	 * Create an iterator which selects from all the components in the page.
	 * @param page the reference page for selector
	 * @param selector the selector string
	 */
	public EntryIterator(EntryContainer<E> container, String selector, Map<String, PseudoClassDef<E>> psudoClassDefs){
		this(container, null, selector, psudoClassDefs);
	}
	
	/**
	 * Create an iterator which selects from all the descendants of a given
	 * component, including itself.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 */
	public EntryIterator(Entry<E> root, String selector, Map<String, PseudoClassDef<E>> psudoClassDefs){
		this(null, root, selector, psudoClassDefs);
	}
	
	private EntryIterator(EntryContainer<E> container, Entry<E> root, String selector, Map<String, PseudoClassDef<E>> psudoClassDefs){
		if (container == null && root == null) 
			throw new IllegalArgumentException(
					"both container and root are null.");
		
		Arguments.checkNotEmpty(selector, "Selector string cannot be empty.");
		
		if(psudoClassDefs!=null)
			_localDefs.putAll(psudoClassDefs);
		
		_selectorList = new Parser().parse(selector);
		if (_selectorList.isEmpty())
			throw new IllegalStateException("Empty selector");
		
		_posOffset = getCommonSeqLength(_selectorList);
		_allIds = isAllIds(_selectorList, _posOffset);
		
		_root = root;
		_container = container;
	}
	
	private static int getCommonSeqLength(List<Selector> list) {
		List<String> strs = null;
		int max = 0;
		for (Selector selector : list) {
			if (strs == null) {
				strs = new ArrayList<String>();
				for (SimpleSelectorSequence seq : selector)
					if (!Strings.isEmpty(seq.getId())) {
						strs.add(seq.toString());
						strs.add(seq.getCombinator().toString());
					} else
						break;
				max = strs.size();
			} else {
				int i = 0;
				for (SimpleSelectorSequence seq : selector)
					if (i >= max || Strings.isEmpty(seq.getId()) || 
							!strs.get(i++).equals(seq.toString()) || 
							!strs.get(i++).equals(seq.getCombinator().toString()))
						break;
				if (i-- < max)
					max = i;
			}
		}
		return (max + 1) / 2;
	}
	private static boolean isAllIds(List<Selector> list, int offset) {
		for (Selector s : list)
			if (s.size() > offset)
				return false;
		return true;
	}
	
	// custom pseudo class definition //
	/**
	 * Add or set pseudo class definition.
	 * @param name the pseudo class name
	 * @param def the pseudo class definition
	 */
	public void setPseudoClassDef(String name, PseudoClassDef def){
		_localDefs.put(name, def);
	}
	
	/**
	 * Remove a pseudo class definition.
	 * @param name the pseudo class name
	 * @return the original definition
	 */
	public PseudoClassDef removePseudoClassDef(String name){
		return _localDefs.remove(name);
	}
	
	/**
	 * Clear all custom pseudo class definitions.
	 */
	public void clearPseudoClassDefs(){
		_localDefs.clear();
	}
	
	
	
	// iterator //
	private boolean _ready = false;
	private Entry<E> _next;
	private int _index = -1;
	
	/**
	 * Return true if it has next component.
	 */
	public boolean hasNext() {
		loadNext();
		return _next != null;
	}
	
	/**
	 * Return the next matched component. A NoSuchElementException will be 
	 * throw if next component is not available.
	 */
	public Entry<E> next() {
		if(!hasNext()) throw new NoSuchElementException();
		_ready = false;
		return _next;
	}
	
	/**
	 * Throws UnsupportedOperationException.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the next matched component, but the iteration is not proceeded.
	 */
	public Entry<E> peek() {
		if(!hasNext()) throw new NoSuchElementException();
		return _next;
	}
	
	/**
	 * Return the index of the next component.
	 */
	public int nextIndex() {
		return _ready ? _index : _index+1;
	}
	
	
	
	// helper //
	private void loadNext(){
		if(_ready) return;
		_next = seekNext();
		_ready = true;
	}
	
	private Entry<E> seekNext() {
		_currCtx = _index < 0 ? buildRootCtx() : buildNextCtx();
		
		while (_currCtx != null && !_currCtx.isMatched()) {
			_currCtx = buildNextCtx();
		}
			
		if (_currCtx != null) {
			_index++;
			return _currCtx.getEntry();
		}
		return null;
	}
	
	private MatchCtxImpl<E> buildRootCtx() {
		Entry<E> rt = _root == null ? 
				_container.getFirstRoot() : _root;
		
//		if (_posOffset > 0) {
//			Selector selector = _selectorList.get(0);
//			for (int i = 0; i < _posOffset; i++) {
//				SimpleSelectorSequence seq = selector.get(i);
//				Entry<E> rt2 = rt.getFellowIfAny(seq.getId());
//				
//				if (rt2 == null)
//					return null;
//				
//				// match local properties
//				if (!EntryLocalProperties.matchType(rt2, seq.getType()) || 
//						!EntryLocalProperties.matchClasses(
//								rt2, seq.getClasses()) ||
//						!EntryLocalProperties.matchAttributes(
//								rt2, seq.getAttributes()) ||
//						!EntryLocalProperties.matchPseudoClasses(
//								rt2, seq.getPseudoClasses(), _localDefs))
//					return null;
//				
//				// check combinator for second and later jumps
//				if (i > 0) {
//					switch (selector.getCombinator(i - 1)) {
//					case DESCENDANT:
//						if (!isDescendant(rt2, rt))
//							return null;
//						break;
//					case CHILD:
//						if (rt2.getParent() != rt)
//							return null;
//						break;
//					case GENERAL_SIBLING:
//						if (!isGeneralSibling(rt2, rt))
//							return null;
//						break;
//					case ADJACENT_SIBLING:
//						if (rt2.getPreviousSibling() != rt)
//							return null;
//						break;
//					}
//				}
//				rt = rt2;
//			}
//			_offsetRoot = rt.getParent();
//		}
				
		MatchCtxImpl<E> ctx = new MatchCtxImpl<E>(rt, _selectorList);
		
//		if (_posOffset > 0)
//			for (Selector selector : _selectorList)
//				ctx.setQualified(selector.getSelectorIndex(), _posOffset - 1);
//		else
			matchLevel0(ctx);
		if(IS_DEBUG){
			System.out.println(">>>>buildRootCtx()");
			System.out.println(ctx); // TODO: debugger	
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
	private static final boolean IS_DEBUG = true;
	
	private MatchCtx<E> buildNextCtx() {
		
//		if (_allIds)
//			return null;
		// TODO: how to skip tree branches
		
		if (_currCtx.getEntry().getFirstChild() != null) 
			return buildFirstChildCtx(_currCtx);
		
		while (_currCtx.getEntry().getNextSibling() == null) {
			_currCtx = _currCtx.getParent();
			if(_currCtx == null || _currCtx.getEntry() == 
					(_posOffset > 0 ? _offsetRoot : _root))
				return null; // reached root
		}
		
		return buildNextSiblingCtx(_currCtx);
	}
	
	private MatchCtxImpl<E> buildFirstChildCtx(MatchCtx<E> parent) {
		
		MatchCtxImpl<E> ctx = new MatchCtxImpl<E>(
				parent.getEntry().getFirstChild(), parent);
		
		// for the first matches, without 
		if (_posOffset == 0 )
			matchLevel0(ctx);
		 
		
		for (Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();
			int seqIdxStart = _posOffset > 0 ? _posOffset - 1 : 0;
			Combinator cb;
			for (int j = seqIdxStart, k=selector.size() - 1; j < k; j++) {
//				if(ctx.isQualified(i, j))
//					continue;//already qualified, no need to update.
				
				// start from this line, we have to test if there's any matches
				// of ctx and selectorSequence.

				cb = selector.getCombinator(j);
				if(IS_DEBUG){
					System.out.println(">>> cb:"+cb+" seq:"+selector.get(j));	
				}
				switch (cb) {
				case DESCENDANT:
					if (parent.isQualified(i, j) && checkIdSpace(selector, j+1, ctx))
						ctx.setQualified(i, j);
					// no break
				case CHILD:
					// TODO: has to decide where to inherit from parent, or to start a new empty one.
					
					if (parent.isQualified(i, j) && match(selector, ctx, j+1)) 
						ctx.setQualified(i, j+1);
					break;
					
				default:
					break;
				}
			}
		}
		if(IS_DEBUG){
			System.out.println(">>>>buildFirstChildCtx()");
			System.out.println("parent = "+parent); // TODO: debugger
			System.out.println(ctx); // TODO: debugger
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
	private MatchCtx<E> buildNextSiblingCtx(MatchCtx<E> ctx) {
		
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		ctrl.moveToNextSibling();
		
		for (Selector selector : _selectorList) {
			int i = selector.getSelectorIndex();// it's like an identifier of current selector in qualified array. 
			int posEnd = _posOffset > 0 ? _posOffset - 1 : 0;
			int len = selector.size();
			
			// clear last position, may be overridden later
			ctrl.setQualified(i, len - 1, false);
			
			for (int j = len - 2; j >= posEnd; j--) {
				Combinator cb = selector.getCombinator(j);
				MatchCtx<E> parent = ctx.getParent();
				
				switch (cb) {
				case DESCENDANT:
					boolean parentPass = parent != null && parent.isQualified(i, j);
					ctrl.setQualified(i, j, 
							parentPass && checkIdSpace(selector, j+1, ctx));
					if (parentPass && match(selector, ctx, j+1))
						ctrl.setQualified(i, j+1);
					break;
				case CHILD:
					ctrl.setQualified(i, j+1, parent != null && 
							parent.isQualified(i, j) && match(selector, ctx, j+1));
					break;
				case GENERAL_SIBLING:
					if (ctx.isQualified(i, j)) 
						ctrl.setQualified(i, j+1, match(selector, ctx, j+1));
					break;
				case ADJACENT_SIBLING:
					ctrl.setQualified(i, j+1, ctx.isQualified(i, j) && 
							match(selector, ctx, j+1));
					ctrl.setQualified(i, j, false);
				}
			}
		}
		
		if (_posOffset == 0)
			matchLevel0(ctx);
		if(IS_DEBUG){
			System.out.println(">>>>buildNextSiblingCtx()");
			System.out.println(ctx); // TODO: debugger
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
	private static boolean checkIdSpace(Selector selector, int index, 
			MatchCtx ctx) {
//		return !selector.requiresIdSpace(index) || 
//			!(ctx.getComponent() instanceof IdSpace);
		return true;
	}
	
//	private static boolean isDescendant(Entry c1, Entry c2) {
//		if (c1 == c2)
//			return true; // first c1 can be IdSpace
//		while ((c1 = c1.getParent()) != null) {
//			if (c1 == c2)
//				return true;
//			if (c1 instanceof IdSpace)
//				return c1 == c2;
//		}
//		return false;
//	}
//	private static boolean isGeneralSibling(Entry c1, Entry c2) {
//		while (c1 != null) {
//			if (c1 == c2)
//				return true;
//			c1 = c1.getPreviousSibling();
//		}
//		return false;
//	}
	// set seq[0] = true if current has a match. 
	private void matchLevel0(MatchCtx<E> ctx) {
		for (Selector selector : _selectorList){
			if(ctx.isQualified(selector.getSelectorIndex(), 0))
				continue;//already qualified, no need to update.
			if (match(selector, ctx, 0)){
				((MatchCtxCtrl)ctx).setQualified(selector.getSelectorIndex(), 0);
			}
		}
			
	}
	
	private boolean match(Selector selector, MatchCtx<E> ctx, int seqIdx) {
		boolean matches =  ctx.match(selector.get(seqIdx), _localDefs);
		return matches;
	}
	
}
