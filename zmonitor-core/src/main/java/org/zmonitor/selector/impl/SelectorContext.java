/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SelSequence;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorContext<E> implements MatchCtx<E> {

	private final List<Selector> selectors;
	private final Map<String, PseudoClassDef> defs;
	private final SelectorContext<E> parent;
	
	private final Entry<E> entry;
	
	private final MatchState[] matchStateArray;	
	
	private final SelectorContext<E> previousSibling;//for debug only.
	private SelectorContext<E> firstChild;// for debug only.
	private SelectorContext<E> nextSibling;// for debug only.
	

	/**
	 * 
	 * @param selectors
	 * @param defs
	 * @param entry
	 */
	private SelectorContext(List<Selector> selectors, //for root
			Map<String, PseudoClassDef> defs, 
			Entry<E> entry) {//Root
		Arguments.checkNotNull(entry);
		Arguments.checkNotNull(selectors);
		Arguments.checkNotNull(defs);
		
		this.entry = entry;
		
		this.selectors = selectors;
		matchStateArray = new MatchState[selectors.size()];
		this.defs = defs;
		this.parent = null;
		this.previousSibling = null;
	}
	/**
	 * 
	 * @param entry
	 * @param matchStateArray
	 * @param parent
	 * @param previousSibling
	 */
	private SelectorContext(Entry<E> entry, 
			SelectorContext<E> parent,
			SelectorContext<E> previousSibling) {//others
		
		Arguments.checkNotNull(entry);
		this.entry = entry;
		this.parent = parent;
		this.previousSibling = previousSibling;
		this.selectors = parent.selectors;
		this.defs = parent.defs;
		this.matchStateArray = new MatchState[selectors.size()];
	}
	
	
	
	public MatchState getMatchState(int selectorIndex) {
		return matchStateArray[selectorIndex];
	}
	
	/**
	 * 
	 * @param entry
	 * @param selectors
	 * @param defs
	 * @return
	 * @throws TerminateMatchException
	 */
	public static<E> SelectorContext<E> toRoot(Entry<E> entry, 
			List<Selector> selectors, 
			Map<String, PseudoClassDef> defs){
		SelectorContext<E> root = new SelectorContext<E>(selectors, defs, entry);
		int counter = 0;
		for(Selector selector : selectors){
			try{
				root.matchStateArray[selector.getSelectorIndex()] = 
						MatchState.toRoot(root, selector);	
			}catch(TerminateMatchException e){
				counter++;
			}
		}
		if(counter==selectors.size())
			throw new TerminateMatchException();
		return root;
	}
	
	
	public SelectorContext<E> toFirstChild(){// this is parent
		firstChild = new SelectorContext<E>(
				entry.getFirstChild(), this, null);
		// init matchStateArray...
		int selIdx;
		int counter = 0;
		for(Selector selector : selectors){
			selIdx = selector.getSelectorIndex();
			try{
				firstChild.matchStateArray[selIdx] = 
						this.matchStateArray[selIdx].toFirstChild(firstChild);
			}catch(TerminateMatchException e){
				counter++;
			}
		}
		if(counter==selectors.size())
			throw new TerminateMatchException();
		return firstChild;
	}
	
	public SelectorContext<E> toNextSibling(){// this is previous sibling
		nextSibling = new SelectorContext<E>(
				entry.getNextSibling(), this.getParent(), this);
		// init matchStateArray...
		int selIdx;
		int counter = 0;
		for(Selector selector : selectors){
			selIdx = selector.getSelectorIndex();
			try{
				nextSibling.matchStateArray[selIdx] = 
						this.matchStateArray[selIdx].toNextSibling(nextSibling);
			}catch(TerminateMatchException e){
				counter++;
			}
		}
		if(counter==selectors.size())
			throw new TerminateMatchException();
		return nextSibling;
	}
	

	
	
	public boolean isEntryMatches(SelSequence sequence) {
		if(sequence==null)return false;
		return EntryLocalProperties.match(entry, sequence, defs);
	}
	
	
	public String toString() {
		return Arrays.toString(matchStateArray) +
				", entry=" + entry ;
	}

	public SelectorContext<E> getParent() {
		return parent;
	}
	public SelectorContext<E> getPreviousSibling() {
		return previousSibling;
	}
	public MatchState[] getMatchStateArray() {
		return matchStateArray;
	}
	public SelectorContext<E> getFirstChild() {
		return firstChild;
	}
	public SelectorContext<E> getNextSibling() {
		return nextSibling;
	}

	public boolean isMatched() {
		for(MatchState matchState : matchStateArray)
			if(matchState.isMatched())return true;
		return false;
	}

	public Entry<E> getEntry() {
		return entry;
	}

	//------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public static void printTree(SelectorContext current, String indent) {

		StringBuffer sb = new StringBuffer();
		int lv = 0;

		SelectorContext next = current;

		AD: while (next != null) {// DFS
			Strings.appendln(sb, toIndent(lv, indent), next.toString());

			if (next.getFirstChild() != null) {// search child first
				next = next.getFirstChild();
				lv++;
			} else {// no child, search next sibling.
				while (next.getNextSibling() == null) {
					next = next.getParent();
					lv--;
					if (next == null || next == current)
						break AD;
				}
				next = next.getNextSibling();
			}
		}

		System.out.println("---------------------------------");
		System.out.println(sb);
		System.out.println("---------------------------------");
	}

	private static String toIndent(int lv, String indent) {
		if (lv == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= lv; i++) {
			sb.append(indent);
		}
		return sb.toString();
	}

}
