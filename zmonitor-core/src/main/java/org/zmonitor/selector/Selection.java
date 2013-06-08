/**
 * 
 */
package org.zmonitor.selector;

import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.zmonitor.util.Predicate;

/**
 * <p>
 * 	A selection represent a set of selected monitor points.
 * 
 * <p>
 * The selection API is a mixture of {@link Iterator} API 
 * and a bunch of criteria operations; The concept of criteria operations 
 * are borrowed from:<i>com.google.common.collect.Iterators</i>. 
 * 
 * <p>
 * The iteration of a selection only iterate the selected monitor point itself,  
 * to traverse every child of selected monitor points, please take a look
 * at {@code #traverse()}. 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Selection<T, R extends Selection<T, R>> {
	/**
	 * 
	 * @return true if there's next element, false otherwise
	 */
	boolean hasNext();
	/**
	 * 
	 * @return iterate to the next element
	 * @throws NoSuchElementException if there's no more elements but this method has been called.
	 */
	T toNext();
	/**
	 * 
	 * @return a list of the rest elements of this selection.
	 */
	List<T> toList();
	/**
	 * Returns the elements of {@code unfiltered} that satisfy a 
	 * predicate.
	 */
	R filter(Predicate<T> predicate);
//	/**
//	 * 
//	 * @param attribute
//	 * @param value
//	 * @return
//	 */
//	Selection<T> eq(String attribute, Class<?> type, Object value);
	
	/**
	 * 
	 * @param JQuery selector format string
	 * @return a new Selection based on given selector.
	 */
	R select(String selector);
	
	/**
	 * Create a new selection which iterate & traverse every selected element through Depth First Search algorithm.  
	 * @param predicate determine if the incoming element is good to iterate.   
	 * @return a new Selection which iterate both selected monitor points and their children.
	 */
	R traverse(Predicate<T> predicate);
	/**
	 * Returns the first element in the selection that satisfies the given
	 * predicate. If no such element is found, null will be returned from this
	 * method and the selection will be left exhausted: its {@code hasNext()}
	 * method will return {@code false}.
	 */
	T find(Predicate<T> predicate);
	/**
	 * Returns {@code true} if one or more elements returned by this selection 
	 * satisfy the given predicate.
	 */
	boolean any(Predicate<T> predicate);
	/**
	 * Returns {@code true} if every element of this selection
	 * satisfies the given predicate. If this selection is empty, {@code true}
	 * is returned.
	 */
	boolean all(Predicate<T> predicate);
	/**
	   * Returns the index of the first element that satisfies
	   * the provided {@code predicate}, or {@code -1} if there's no such
	   * elements.
	   *
	   * <p>If -1 is returned, the selection will be left exhausted: its
	   * {@code hasNext()} method will return {@code false}.  Otherwise,
	   * the selection will be set to the element which satisfies the
	   * {@code predicate}.
	   *
	   */
	int indexOf( Predicate<T> predicate);
	
	/**
	   * Returns the number of elements remaining in this selection. The selection
	   * will be left exhausted: its {@code hasNext()} method will return
	   * {@code false}.
	   */
	int size();
}
