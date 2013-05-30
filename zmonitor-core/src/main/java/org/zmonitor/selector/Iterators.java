/**
 * 
 */
package org.zmonitor.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.zm.MPWrapper;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.util.AbstractIterator;
import org.zmonitor.util.Predicate;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Iterators {
//	/**
//	 * 
//	 * @author Ian YT Tsai(Zanyking)
//	 *
//	 */
//	public interface Visitor<T>{
//		/**
//		 * 
//		 * @param mp the monitor point 
//		 * @return true, if the visits should continue, false to break it.
//		 */
//		public boolean visit(T mp);
//	}
//	/**
//	 * 
//	 * @param ms
//	 * @param selector
//	 * @param visitor
//	 */
//	public static void visit(MonitorSequence ms, String selector, Visitor<MonitorPoint> visitor){
//		Iterator<Entry> iterator = toIterator(ms, selector);
//		boolean shouldContinue = true;
//		while(iterator.hasNext()){
//			shouldContinue = visitor.visit((MonitorPoint) iterator.next().getObject());
//			if(!shouldContinue){
//				break;
//			}
//		}
//	}


	
	/**
	 * 
	 * @param unfiltered
	 * @param predicate
	 * @return
	 */
	public static Iterator<Entry<MonitorPoint>> filter(
			final Iterator<Entry<MonitorPoint>> unfiltered, 
			 final Predicate<? super Entry<MonitorPoint>> predicate){
		checkNotNull(unfiltered);
		checkNotNull(predicate);
		return new AbstractIterator<Entry<MonitorPoint>>(){
			protected Entry<MonitorPoint> computeNext() {
				while (unfiltered.hasNext()) {
					Entry<MonitorPoint> element = unfiltered.next();
					if (predicate.apply(element)) {
						return element;
					}
				}
				return endOfData();
			}
		};
	}
	/**
	   * Returns the first element in {@code iterator} that satisfies the given
	   * predicate.  If no such element is found, null will be returned from this 
	   * method and the iterator will be left exhausted: its
	   * {@code hasNext()} method will return {@code false}.
	   *
	   */
	  public static Entry<MonitorPoint> find(Iterator<Entry<MonitorPoint>> iterator, 
			  Predicate<? super Entry<MonitorPoint>> predicate) {
	    Iterator<Entry<MonitorPoint>> filteredIterator = filter(iterator, predicate);
	    return filteredIterator.hasNext() ? filteredIterator.next() : null;
	  }
	/**
	 * 
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static boolean any(Iterator<Entry<MonitorPoint>> iterator,
			Predicate<? super Entry<MonitorPoint>> predicate) {
		checkNotNull(predicate);
		while (iterator.hasNext()) {
			Entry<MonitorPoint> element = iterator.next();
			if (predicate.apply(element)) {
				return true;
			}
		}
		return false;
	}
	  /**
	   * Returns the index in {@code iterator} of the first element that satisfies
	   * the provided {@code predicate}, or {@code -1} if the Iterator has no such
	   * elements.
	   *
	   * <p>More formally, returns the lowest index {@code i} such that
	   * {@code predicate.apply(Iterators.get(iterator, i))} returns {@code true},
	   * or {@code -1} if there is no such index.
	   *
	   * <p>If -1 is returned, the iterator will be left exhausted: its
	   * {@code hasNext()} method will return {@code false}.  Otherwise,
	   * the iterator will be set to the element which satisfies the
	   * {@code predicate}.
	   *
	   */
	public static int indexOf(Iterator<Entry<MonitorPoint>> iterator,
			Predicate<? super Entry<MonitorPoint>> predicate) {

		checkNotNull(predicate, "predicate");
		int i = 0;
		while (iterator.hasNext()) {
			Entry<MonitorPoint> current = iterator.next();
			if (predicate.apply(current)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	/**
	 * Ensures that an object reference passed as a parameter to the calling
	 * method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @param errorMessage
	 *            the exception message to use if the check fails; will be
	 *            converted to a string using {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws NullPointerException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return reference;
	}
	  
	  
	private static <T> T checkNotNull(T ref){
		if(ref==null)
			throw new NullPointerException();
		return ref;
	}
	

	//--------------------------- Selector Related ------------------
	/**
	 * 
	 * @param ms
	 * @param selector
	 * @return
	 */
	public static Iterator<Entry<MonitorPoint>> toIterator(MonitorSequence ms, String selector){
		return new EntryIterator(
				new MSWrapper(ms), selector);
	}
	/**
	 * 
	 * @param mp
	 * @param selector
	 * @return
	 */
	public static Iterator<Entry<MonitorPoint>> toIterator(MonitorPoint mp, String selector){
		return new EntryIterator(
				new MPWrapper(mp, null), selector);
	}
	/**
	 * 
	 * @param mp
	 * @param selector
	 * @return
	 */
	public static List<MonitorPoint> find(MonitorPoint mp,  String selector){
		return toList(toIterator(mp, selector));
	}
	/**
	 * 
	 * @param ms
	 * @param selector
	 * @return
	 */
	public static List<MonitorPoint> find(MonitorSequence ms, String selector){
		return toList(toIterator(ms, selector));
	}
	private static List<MonitorPoint> toList(Iterator<Entry<MonitorPoint>> iterator){
		List<MonitorPoint> result = new ArrayList<MonitorPoint>();
		while(iterator.hasNext()){
			result.add((MonitorPoint) iterator.next().getObject());
		}
		return result;
	}
	
	
	
	
	
}
