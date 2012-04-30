/**ArrayBag.java
 * 2011/3/17
 * 
 */
package org.zkoss.monitor.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ArrayBag <T extends Comparable<? super T>> implements Collection<T>, Serializable{
	private static final long serialVersionUID = -5826185174214376115L;
	
	protected ArrayList<T> arr = new ArrayList<T>();
	private boolean modified;

	public int size() {
		return arr.size();
	}

	public boolean isEmpty() {
		return arr.isEmpty();
	}

	public boolean contains(Object o) {
		return arr.contains(o);
	}
	
	public Iterator<T> iterator() {
		return new ArrayList<T>(arr).iterator();
	}

	public boolean containsAll(Collection<?> c) {
		return arr.containsAll(c);
	}

	public Object clone() {
		return arr.clone();
	}

	public Object[] toArray() {
		return arr.toArray();
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return arr.toArray(a);
	}

	public boolean removeAll(Collection<?> c) {
		modified=true;
		return arr.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		modified=true;
		return arr.retainAll(c);
	}

	public boolean add(T e) {
		modified=true;
		return arr.add(e);
	}

	

	public boolean remove(Object o) {
		modified=true;
		return arr.remove(o);
	}

	public void clear() {
		modified=true;
		arr.clear();
	}

	public boolean addAll(Collection<? extends T> c) {
		modified=true;
		return arr.addAll(c);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arr == null) ? 0 : arr.hashCode());
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayBag<T> other = (ArrayBag) obj;
		if (arr == null) {
			if (other.arr != null)
				return false;
		} else{
			this.refresh();
			other.refresh();
			if (!arr.equals(other.arr))
				return false;
		}
			
		return true;
	}
	
	public String toString() {
		refresh();
		return arr.toString();
	}
	private void refresh(){
		if(arr.size()>0 && modified){
			Collections.sort(arr);
			modified = false;
		}
	}
	
	
	
}
