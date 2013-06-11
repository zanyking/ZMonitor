/**
 * 
 */
package org.zmonitor.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.PseudoClassDef;

/**
 * A collection of selector related utilities. 
 * @author simonpai, Ian YT Tsai(Zanyking)
 */
public class Selectors {
	
	
	/**
	 * Returns an iterator that iterates through all Components matched by the
	 * selector. 
	 * @param container the reference container for selector
	 * @param selector the selector string
	 * @return an Iterator of Component
	 */
	public static<T> EntryIterator<T> iterator(EntryContainer<T> container, String selector, Map<String, PseudoClassDef<T>> psudoClassDefs) {
		return new EntryIterator<T>(container, selector, psudoClassDefs);
	}
	
	/**
	 * Returns an iterator that iterates through all Entrys matched by the
	 * selector. 
	 * @param root the reference Entry for selector
	 * @param selector the selector string
	 * @return an Iterator of Entry
	 */
	public static<T> EntryIterator<T> iterator(Entry<T> root, String selector, Map<String, PseudoClassDef<T>> psudoClassDefs){
		return new EntryIterator<T>(root, selector, psudoClassDefs);
	}
	
//	/**
//	 * Returns a list of Entrys that match the selector.
//	 * @param container the reference container for selector
//	 * @param selector the selector string
//	 * @return a List of Entry
//	 */
//	public static<T> List<Entry<T>> find(EntryContainer<T> container, String selector) {
//		return toList(iterator(container, selector));
//	}
//	
//	/**
//	 * Returns a list of Entrys that match the selector.
//	 * @param root the reference Entry for selector
//	 * @param selector the selector string
//	 * @return a List of Entry
//	 */
//	public static<T> List<Entry<T>> find(Entry<T> root, String selector) {
//		return toList(iterator(root, selector));
//	}
//	
//	/**
//	 * Returns the ith Entry that matches the selector
//	 * @param container the reference container for selector
//	 * @param selector the selector string
//	 * @param index 1-based index (1 means the first Entry found)
//	 * @return Entry, null if not found
//	 */
//	public static<T> Entry<T> find(EntryContainer<T> container, String selector, int index) {
//		return getIthItem(new EntryIterator<T>(container, selector), index);
//	}
//	
//	/**
//	 * Returns the ith Entry that matches the selector
//	 * @param root root the reference Entry for selector
//	 * @param selector selector the selector string
//	 * @param index 1-based index (1 means the first Entry found)
//	 * @return Entry, null if not found
//	 */
//	public static<T> Entry<T> find(Entry<T> root, String selector, int index) {
//		return getIthItem(new EntryIterator<T>(root, selector), index);
//	}
	/**
	 * 
	 * @param iterator
	 * @return
	 */
	public static<T> List<T> toValueList(Iterator<Entry<T>> iterator){
		List<T> result = new ArrayList<T>();
		while(iterator.hasNext()){
			result.add((T) iterator.next().getValue());
		}
		return result;
	}
	
	private static <T> List<T> toList(Iterator<T> iterator){
		List<T> result = new ArrayList<T>();
		while(iterator.hasNext()) 
			result.add(iterator.next());
		
		return result;
	}
	private static <T> T getIthItem(Iterator<T> iter, int index){
		// shift (index - 1) times
		for(int i = 1; i < index; i++) {
			if(!iter.hasNext())
				return null;
			iter.next();
		}
		return iter.hasNext() ? iter.next() : null;
	}
	
}
