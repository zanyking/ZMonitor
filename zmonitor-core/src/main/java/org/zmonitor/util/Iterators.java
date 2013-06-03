/**
 * 
 */
package org.zmonitor.util;

import java.util.Iterator;


/**
 * 
 * A utility class which borrowed the code from Google Guava. 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Iterators {

	/**
	 * Returns the elements of {@code unfiltered} that satisfy a 
	 * predicate.
	 */
	public static <T> Iterator<T> filter(final Iterator<T> unfiltered,
			final Predicate<? super T> predicate) {
		checkNotNull(unfiltered);
		checkNotNull(predicate);
		return new AbstractIterator<T>() {
			protected T computeNext() {
				while (unfiltered.hasNext()) {
					T element = unfiltered.next();
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
	 * predicate. If no such element is found, null will be returned from this
	 * method and the iterator will be left exhausted: its {@code hasNext()}
	 * method will return {@code false}.
	 */
	public static <T> T find(Iterator<T> iterator,
			Predicate<? super T> predicate) {
		Iterator<T> filteredIterator = filter(iterator, predicate);
		return filteredIterator.hasNext() ? filteredIterator.next() : null;
	}

	/**
	 * Returns {@code true} if one or more elements returned by {@code iterator}
	 * satisfy the given predicate.
	 */
	public static <T> boolean any(Iterator<T> iterator,
			Predicate<? super T> predicate) {
		checkNotNull(predicate);
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (predicate.apply(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns {@code true} if every element returned by {@code iterator}
	 * satisfies the given predicate. If {@code iterator} is empty, {@code true}
	 * is returned.
	 */
	public static <T> boolean all(Iterator<T> iterator,
			Predicate<? super T> predicate) {
		checkNotNull(predicate);
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (!predicate.apply(element)) {
				return false;
			}
		}
		return true;
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
	public static<T> int indexOf(Iterator<T> iterator,
			Predicate<? super T> predicate) {

		checkNotNull(predicate, "predicate");
		int i = 0;
		while (iterator.hasNext()) {
			T current = iterator.next();
			if (predicate.apply(current)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
	
	
	
	 /**
	   * Returns the number of elements remaining in {@code iterator}. The iterator
	   * will be left exhausted: its {@code hasNext()} method will return
	   * {@code false}.
	   */
	  public static int size(Iterator<?> iterator) {
	    int count = 0;
	    while (iterator.hasNext()) {
	      iterator.next();
	      count++;
	    }
	    return count;
	  }
	
	
	  /**
	   * Returns a string representation of {@code iterator}, with the format
	   * {@code [e1, e2, ..., en]}. The iterator will be left exhausted: its
	   * {@code hasNext()} method will return {@code false}.
	   */
	  public static String toString(Iterator<?> iterator) {
	    if (!iterator.hasNext()) {
	      return "[]";
	    }
	    StringBuilder builder = new StringBuilder();
	    builder.append('[').append(iterator.next());
	    while (iterator.hasNext()) {
	      builder.append(", ").append(iterator.next());
	    }
	    return builder.append(']').toString();
	  }
	
	
	
	
	
	// Utilities
	
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
	public static<T> T checkNotNull(T reference, Object errorMessage) {
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
	
}
