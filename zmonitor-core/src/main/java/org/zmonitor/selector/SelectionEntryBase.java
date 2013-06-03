/**
 * 
 */
package org.zmonitor.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.zmonitor.util.Iterators;
import org.zmonitor.util.Predicate;


/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class SelectionEntryBase<T> implements Selection<T>, Iterator<Entry<T>>{

	protected final Iterator<Entry<T>> itor;
	
	/**
	 * 
	 * @param itor
	 */
	public SelectionEntryBase(Iterator<Entry<T>> itor){
		this.itor = itor;
	}
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public SelectionEntryBase(@SuppressWarnings("rawtypes") List list) {
		List<Entry<T>> entries = null;
		if(list.isEmpty()|| list.get(0) instanceof Entry){
			
			entries = list;
		
		}else if(list.get(0) instanceof EntryContainer){
			entries = new ArrayList<Entry<T>>(list.size());
			EntryContainer<T> container; 
			for(Object obj : list){
				container = (EntryContainer<T>) obj;
				entries.add( container.getFirstRoot());
			}
		}else{
			throw new IllegalArgumentException(" the given List is not correct.");	
		}
		itor = entries.iterator();
	}
	/**
	 * 
	 * @param entry
	 */
	public SelectionEntryBase(Entry<T> entry) {
		List<Entry<T>> entries = new ArrayList<Entry<T>>(1);
		entries.add(entry);
		itor = entries.iterator();
	}
	/**
	 * 
	 * @param container
	 */
	public SelectionEntryBase(EntryContainer<T> container) {
		List<Entry<T>> entries = new ArrayList<Entry<T>>(1);
		entries.add(container.getFirstRoot());
		itor = entries.iterator();
	}
	
	protected SelectionEntryBase<T> toSelection(Iterator<Entry<T>> itor){
		return new SelectionEntryBase<T>( itor);
	}
	
	public boolean hasNext() {
		return itor.hasNext();
	}
	public Entry<T> next() {
		return itor.next();
	}
	public void remove() {
		throw new UnsupportedOperationException("this is an immutable itertator");
	}
	

	
	
	//========================Operations===============================
	
	/**
	 * call iterator's next() and return the object of Entry.<br>
	 * please use hasNext to verify if there's any entry next.
	 * @return the object of next entry. 
	 */
	public T toNext(){
		return next().getValue();
	}
	
	
	
	public List<T> toList(){
		return Selectors.toValueList(itor);
	}

	public SelectionEntryBase<T> filter(
			Predicate<T> predicate){
		return toSelection(Iterators.filter(this, 
				new AdaptionPredicate<T>(predicate)));
	}

	public SelectionEntryBase<T> select(String selector){
		return toSelection(
			new NestedSelectorIterator<T>(this, selector));
	}

	public T find(Predicate<T> predicate){
		Entry<T> e = Iterators.find(this, 
				new AdaptionPredicate<T>(predicate));
		return e.getValue();
	}
	
	public boolean any(Predicate<T> predicate){
		return Iterators.any(this, 
				new AdaptionPredicate<T>(predicate));
	}

	public int indexOf( Predicate<T> predicate){
		return Iterators.indexOf(this, 
				new AdaptionPredicate<T>(predicate));
	}
	public boolean all(Predicate<T> predicate) {
		return Iterators.all(this, 
				new AdaptionPredicate<T>(predicate));
	}
	public int size() {
		return Iterators.size(this);
	}
	
}//end of class...

/**
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <T>
 */
class AdaptionPredicate<T> implements Predicate<Entry<T>>{
	private final Predicate<T> predicate;
	public AdaptionPredicate(Predicate<T> predicate) {
		this.predicate = predicate;
	}
	public boolean apply(Entry<T> input) {
		return predicate.apply( input.getValue());
	}
}//end of class...

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <T>
 */
class NestedSelectorIterator<T> implements Iterator<Entry<T>>{
		private final Iterator<Entry<T>> origin; 
		private final String selector;
		/**
		 * 
		 * @param origin
		 * @param selector
		 */
		public NestedSelectorIterator(Iterator<Entry<T>> origin, String selector) {
			this.origin = origin;
			this.selector = selector;
		}

		private Iterator<Entry<T>> sub;
		private boolean fetched = false;
		private Entry<T> next;
		
		public boolean hasNext() {
			loadNext();
			return next != null;
		}

		public Entry<T> next() {
			if(!hasNext()) throw new NoSuchElementException();
			fetched = false;
			return next;
		}
		
		private void loadNext(){
			if(fetched) return;
			next = seekNext();
			fetched = true;
		}
		private Entry<T> seekNext(){
			while(sub==null || !sub.hasNext()){ 
				
				if(!origin.hasNext())return null;
				sub = Selectors.iterator(origin.next(), selector) ;
				
				if(sub.hasNext()){
					return sub.next();
				}
			}
			return null;
		}
		
		public void remove() {
			throw new UnsupportedOperationException("this is an immutable itertator");
		}
}//end of class...
