/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;

/**
 * An implementation of Iterator&lt;Component> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * @author simonpai
 */
public class EntryIterator implements Iterator<Entry> {
	
	private Entry root;
	private EntryContainer container;
	private List<Selector> selectorList;
	private Map<String, PseudoClassDef> localDefs;
	
	private MatchCtx currCtx;
	
	/**
	 * Create an iterator which selects from all the components in the page.
	 * @param container the reference page for selector
	 * @param selector the selector string
	 */
	public EntryIterator(EntryContainer container, String selector){
		this(container, null, selector);
	}
	
	/**
	 * Create an iterator which selects from all the descendants of a given
	 * component, including itself.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 */
	public EntryIterator(Entry root, String selector){
		this(null, root, selector);
	}
	
	private EntryIterator(EntryContainer container, Entry root, String selector){
		if((container == null && root == null) || 
				selector == null || 
				selector.isEmpty()){
			throw new IllegalArgumentException();
		}
		
		this.localDefs = new HashMap<String, PseudoClassDef>();
		this.selectorList = new Parser().parse(selector);
		this.root = root;
		this.container = container;
	}
	
	// custom pseudo class definition //
	/**
	 * Add or set pseudo class definition.
	 * @param name the pseudo class name
	 * @param def the pseudo class definition
	 */
	public void setPseudoClassDef(String name, PseudoClassDef def){
		localDefs.put(name, def);
	}
	
	/**
	 * Remove a pseudo class definition.
	 * @param name the pseudo class name
	 * @return the original definition
	 */
	public PseudoClassDef removePseudoClassDef(String name){
		return localDefs.remove(name);
	}
	
	/**
	 * Clear all custom pseudo class definitions.
	 */
	public void clearPseudoClassDefs(){
		localDefs.clear();
	}
	
	
	
	// iterator //
	private boolean _ready = false;
	private Entry _next;
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
	public Entry next() {
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
	public Entry peek() {
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
	
	private Entry seekNext() {
		currCtx = _index < 0 ? buildRootCtx() : buildNextCtx();
		
		while(currCtx != null && !currCtx.isMatched()) {
			currCtx = buildNextCtx();
		}

		if(currCtx != null) {
			_index++;
			return currCtx.getEntry();
		}
		return null; 
	}
	
	private MatchCtx buildRootCtx(){
		Entry rt = (root == null) ? 
				container.getFirstRoot(): root;
				
		MatchCtx ctx = new MatchCtxImpl(rt, selectorList);
		matchLevel0(selectorList, ctx);
		return ctx;
	}
	
	private MatchCtx buildNextCtx(){
		
		if(currCtx.getEntry().getFirstChild() != null) 
			return buildFirstChildCtx(currCtx);
		
		while(currCtx.getEntry().getNextSibling() == null) {
			currCtx = currCtx.getParent();
			if(currCtx == null || currCtx.getEntry() == root) 
				return null; // reached root
		}
		
		return buildNextSiblingCtx(currCtx);
	}
	
	private MatchCtx buildFirstChildCtx(MatchCtx parent){
		
		MatchCtx ctx = new MatchCtxImpl(
				parent.getEntry().getFirstChild(), parent);
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		
		matchLevel0(selectorList, ctx);
		
		for(Selector selector : selectorList) {
			int i = selector.getSelectorIndex();
			
			for(int j=0; j < selector.size()-1; j++){
				switch(selector.getCombinator(j)){
				case DESCENDANT:
					if(parent.isQualified(i, j)) ctrl.setQualified(i, j);
					// no break
				case CHILD:
					if(parent.isQualified(i, j) && match(selector, ctx, j+1)) 
						ctrl.setQualified(i, j+1);
					break;
				}
			}
		}
		return ctx;
	}
	
	private MatchCtx buildNextSiblingCtx(MatchCtx ctx){
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		ctrl.moveToNextSibling();
		
		for(Selector selector : selectorList) {
			int i = selector.getSelectorIndex();
			ctrl.setQualified(i, selector.size()-1, 
					match(selector, ctx, selector.size()-1));
			
			for(int j = selector.size() - 2; j > -1; j--){
				Combinator cb = selector.getCombinator(j);
				MatchCtx parent = ctx.getParent();
				
				switch(cb){
				case DESCENDANT:
				case CHILD:
					if(parent != null && parent.isQualified(i, j) && 
							match(selector, ctx, j+1))
						ctrl.setQualified(i, j+1);
					break;
				case GENERAL_SIBLING:
					if(ctx.isQualified(i, j)) 
						ctrl.setQualified(i, j+1, 
								match(selector, ctx, j+1));
					break;
				case ADJACENT_SIBLING:
					ctrl.setQualified(i, j+1, ctx.isQualified(i, j) && 
							match(selector, ctx, j+1));
					ctrl.setQualified(i, j, false);
				}
			}
		}
		
		matchLevel0(selectorList, ctx);
		return ctx;
	}
	
	private void matchLevel0(List<Selector> list, MatchCtx ctx) {
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		for(Selector selector : list)
			if(match(selector, ctx, 0)) 
				ctrl.setQualified(selector.getSelectorIndex(), 0);
	}
	
	private boolean match(Selector selector, MatchCtx ctx, int index) {
		return ctx.match(selector.get(index), localDefs);
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ComponentIterator: \n* index: ").append(_index);
		for(MatchCtx c = currCtx; c != null; c = c.getParent())
			sb.append("\n").append(c);
		return sb.append("\n\n").toString();
	}
	
}
