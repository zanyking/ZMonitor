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
 * An implementation of Iterator&lt;Entry> that realizes the selector matching
 * algorithm. The iteration is lazily evaluated. i.e. The iterator will not
 * perform extra computation until .next() is called.
 * 
 * @author simonpai, Ian YT Tsai(Zanyking)
 */
public class EntryIterator<E> implements Iterator<Entry<E>> {
	
	private Entry<E> root;
	private EntryContainer<E> container;
	
	private List<Selector> selectorList;
	private Map<String, PseudoClassDef> localDefs;
	
	
	
	/**
	 * Create an iterator which selects from all the Entrys in the page.
	 * @param container the reference page for selector
	 * @param selector the selector string
	 */
	public EntryIterator(EntryContainer<E> container, String selector){
		this(container, null, selector);
	}
	
	/**
	 * Create an iterator which selects from all the descendants of a given
	 * Entry, including itself.
	 * @param root the reference Entry for selector
	 * @param selector the selector string
	 */
	public EntryIterator(Entry<E> root, String selector){
		this(null, root, selector);
	}
	
	private EntryIterator(EntryContainer<E> container, Entry<E> root, String selector){
		if(selector == null || selector.isEmpty()){
			throw new IllegalArgumentException("Selector cannot be null or empty! selector:"+ selector);
		}
		if(container == null && root == null){
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
	private boolean fetched = false;
	private Entry<E> next;
	private int index = -1;
	private MatchCtx<E> currCtx;
	
	
	/**
	 * Return true if it has next Entry.
	 */
	public boolean hasNext() {
		loadNext();
		return next != null;
	}
	// helper //
	private void loadNext(){
		if(fetched) return;
		next = seekNext();
		fetched = true;
	}
	/**
	 * Return the next matched Entry. A NoSuchElementException will be 
	 * throw if next Entry is not available.
	 */
	public Entry<E> next() {
		if(!hasNext()) throw new NoSuchElementException();
		fetched = false;
		return next;
	}
	
	/**
	 * Throws UnsupportedOperationException.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the next matched Entry, but the iteration is not proceeded.
	 */
	public Entry<E> peek() {
		if(!hasNext()) throw new NoSuchElementException();
		return next;
	}
	
	/**
	 * Return the index of the next Entry.
	 */
	public int nextIndex() {
		return fetched ? index : index+1;
	}
	
	/*
	 * 
	 *
	 */
	private Entry<E> seekNext() {
		if(currCtx!=null && "A__init__12".equals(currCtx.getEntry().getId())){
			
		}
		currCtx = index < 0 ? //if start from root? 
			initRootCtx() : toNextCtx(currCtx);
		
		//look up the next matched entry.
		while(currCtx != null && !currCtx.isMatched()) {
			currCtx = toNextCtx(currCtx);
		}

		if(currCtx != null) {
			index++;
			return currCtx.getEntry();
		}
		return null; 
	}
	
	private MatchCtx<E> initRootCtx(){
		Entry<E> rt = (root == null) ? 
				container.getFirstRoot(): root;
				
		MatchCtx<E> ctx = new MatchCtxImpl<E>(rt, selectorList);
		matchLevel0(selectorList, ctx);
		return ctx;
	}
	/*
	 * DFS look up for "next" entry.
	 * 1. if this entry has child, return child.
	 * 2. if this entry has next sibling, return next sibling.
	 * 3. otherwise, backward to parent node, and 
	 */
	private MatchCtx<E> toNextCtx(MatchCtx<E> mCtx){//DFS
		
		// if there's first kid, do first kid.
		if(mCtx.getEntry().getFirstChild() != null) 
			return buildFirstChildCtx(mCtx);
		
		//search next sibling
		while(mCtx.getEntry().getNextSibling() == null) {
			//no next sibling, search any ancestor's next sibling.
			mCtx = mCtx.getParent();
			if(mCtx == null || mCtx.getEntry() == root) 
				return null; // reached root
		}
		
		//mCtx.toNext() definitely has value.
		mCtx = initNextCtx(mCtx.toNext());
		
		return mCtx; 
	}
	
	private MatchCtx<E> buildFirstChildCtx(MatchCtx<E> parentCtx){
		
		MatchCtx<E> ctx = new MatchCtxImpl<E>(
				parentCtx.getEntry().getFirstChild(), parentCtx);
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		
		matchLevel0(selectorList, ctx);
		
		for(Selector selector : selectorList) {
			int i = selector.getSelectorIndex();
			
			for(int j=0; j < selector.size()-1; j++){
				switch(selector.getCombinator(j)){
				case DESCENDANT:
					if(parentCtx.isQualified(i, j)) 
						ctrl.setQualified(i, j);
					// no break
				case CHILD:
					if(parentCtx.isQualified(i, j) && 
							match(selector, ctx, j+1)) 
						ctrl.setQualified(i, j+1);
					break;
				}
			}
		}
		return ctx;
	}
	
	private MatchCtx<E> initNextCtx(MatchCtx<E> ctx){
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		
//		ctrl.moveToNextSibling();
		
		for(Selector selector : selectorList) {
			int i = selector.getSelectorIndex();
			
			ctrl.setQualified(i, selector.size()-1, 
					match(selector, ctx, selector.size()-1));
			
			for(int j = selector.size() - 2; j > -1; j--){
				Combinator cb = selector.getCombinator(j);
				MatchCtx<E> parent = ctx.getParent();
				
				switch(cb){
				case DESCENDANT:
				case CHILD:
					if(parent != null && parent.isQualified(i, j) && 
							match(selector, ctx, j+1)){
						ctrl.setQualified(i, j+1);
					}
					break;
				case GENERAL_SIBLING:
					if(ctx.isQualified(i, j)){
						ctrl.setQualified(i, j+1, 
								match(selector, ctx, j+1));
					}
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
	/**
	 * update the qualified matrix of MatchCtx by given Selectors.
	 * @param list
	 * @param ctx
	 */
	private void matchLevel0(List<Selector> list, MatchCtx<E> ctx) {
		MatchCtxCtrl ctrl = (MatchCtxCtrl) ctx;
		for(Selector selector : list)
			if(match(selector, ctx, 0)) 
				ctrl.setQualified(selector.getSelectorIndex(), 0);
	}
	
	private boolean match(Selector selector, MatchCtx<E> ctx, int index) {
		return ctx.match(selector.get(index), localDefs);
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EntryIterator: \n* index: ").append(index);
		for(MatchCtx<E> c = currCtx; c != null; c = c.getParent())
			sb.append("\n").append(c);
		return sb.append("\n\n").toString();
	}
	
}
