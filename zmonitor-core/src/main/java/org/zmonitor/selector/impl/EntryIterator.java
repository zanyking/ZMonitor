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
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.Strings;

/**
 * An implementation of Iterator&lt;Component> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * @author simonpai, Ian YT Tsai
 * 
 */
public class EntryIterator<E> implements Iterator<Entry<E>> {
	
	private final EntryContainer<E> _container;
	private final Entry<E> _root;
	private final List<Selector> _selectorList;
	private final Map<String, PseudoClassDef> _localDefs = 
		new HashMap<String, PseudoClassDef>();
	
	private MatchCtx<E> _currCtx;
	
	/**
	 * Create an iterator which selects from all the components in the page.
	 * @param page the reference page for selector
	 * @param selector the selector string
	 */
	public EntryIterator(EntryContainer<E> container, String selector, 
			Map<String, PseudoClassDef<E>> psudoClassDefs){
		this(container, null, selector, psudoClassDefs);
	}
	
	/**
	 * Create an iterator which selects from all the descendants of a given
	 * component, including itself.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 */
	public EntryIterator(Entry<E> root, String selector, 
			Map<String, PseudoClassDef<E>> psudoClassDefs){
		this(null, root, selector, psudoClassDefs);
	}
	
	private EntryIterator(EntryContainer<E> container, Entry<E> root, 
			String selector, Map<String, PseudoClassDef<E>> psudoClassDefs){
		if (container == null && root == null) 
			throw new IllegalArgumentException(
					"both container and root are null.");
		
		Arguments.checkNotEmpty(selector, "Selector string cannot be empty.");
		
		if(psudoClassDefs!=null)
			_localDefs.putAll(psudoClassDefs);
		
		_selectorList = new Parser().parse(selector);
		if (_selectorList.isEmpty())
			throw new IllegalStateException("Empty selector");
		
		_root = root;
		_container = container;
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
	
	private static final boolean IS_DEBUG = true;
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private MatchCtxImpl<E> buildRootCtx() {
		Entry<E> rt = _root == null ? 
				_container.getFirstRoot() : _root;
		
		MatchCtxImpl<E> ctx = new MatchCtxImpl<E>(rt, _selectorList);
		
		if(IS_DEBUG){
			System.out.println(">>>>buildRootCtx()");
			System.out.println(ctx); // TODO: debugger	
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 */
	private MatchCtx<E> buildNextCtx() {
		
		if (_currCtx.getEntry().getFirstChild() != null) 
			return buildFirstChildCtx(_currCtx);
		
		while (_currCtx.getEntry().getNextSibling() == null) {
			_currCtx = _currCtx.getParent();
			if(_currCtx == null || _currCtx.getEntry() ==_root)
				return null; // reached root
		}
		
		return buildNextSiblingCtx(_currCtx);
	}
	/*
	 * 
	 * 
	 * 
	 */
	private MatchCtxImpl<E> buildFirstChildCtx(MatchCtx<E> parent) {
		
		MatchCtxImpl<E> ctx = new MatchCtxImpl<E>(
				parent.getEntry().getFirstChild(), parent);
		
		if(IS_DEBUG){
			System.out.println(">>>>buildFirstChildCtx()");
			System.out.println("parent = "+parent); // TODO: debugger
			System.out.println(ctx); // TODO: debugger
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
	private MatchCtx<E> buildNextSiblingCtx(MatchCtx<E> ctx) {
		
		if(IS_DEBUG){
			System.out.println(">>>>buildNextSiblingCtx()");
			System.out.println(ctx); // TODO: debugger
			System.out.println("------------------------\n");
		}
		return ctx;
	}
	
}
