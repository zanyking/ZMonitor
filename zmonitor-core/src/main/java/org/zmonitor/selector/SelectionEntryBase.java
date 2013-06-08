/**
 * 
 */
package org.zmonitor.selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.Iterators;
import org.zmonitor.util.Predicate;


/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class SelectionEntryBase<T , R extends Selection<T, R>> implements Selection<T, R>, Iterator<Entry<T>>{

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
	
	protected SelectionEntryBase<T, R> toSelection(Iterator<Entry<T>> itor){
		return new SelectionEntryBase<T, R>( itor);
	}
	
	public boolean hasNext() {
		return itor.hasNext();
	}
	public Entry<T> next() {
		Entry<T> next = itor.next();
		if(next==null)
			throw new IllegalStateException("the next should never be NULL! "+itor);
		return next;
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
	@SuppressWarnings("unchecked")
	public R filter(
			Predicate<T> predicate){
		return (R) toSelection(Iterators.filter(this, 
				new AdaptionPredicate<T>(predicate)));
	}

	@SuppressWarnings("unchecked")
	public R select(String selector){
		return (R) toSelection(
			new NestedSelectorIterator<T>(this, selector));
	}
	
	@SuppressWarnings("unchecked")
	public R traverse(Predicate<T> predicate) {
		return (R) toSelection(
			new DetailIterator<T>(this));
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
class TraverseIterator<T> implements Iterator<Entry<T>>{
	protected final Predicate<T> predicate;
	private boolean fetched = false;
	private Entry<T> next;
	private Entry<T> root;
	
	public TraverseIterator(Entry<T> entry, Predicate<T> predicate) {
		this(predicate);
		this.root = entry;
		this.next = entry;
		
	}
	
	protected TraverseIterator(Predicate<T> predicate){
		this.predicate = predicate;
	}
	public void remove() {
		throw new UnsupportedOperationException("this is an immutable itertator");
	}
	public boolean hasNext() {
		loadNext();
		return next != null;
	}
	
	public Entry<T> peekNext(){
		return next;
	}
	
	public Entry<T> getRoot(){
		return root;
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
	protected Entry<T> seekNext(){
		return dfs( root, next);
	}
	
	private Entry<T> dfs(Entry<T> root, Entry<T> current){
		Entry<T> next = predicate(current.getFirstChild(), CHILD_ALTERNATIVE);
		
		while(next == null){//has no first child, look up next sibling...
			next = predicate(current.getNextSibling(), NEXT_SIBLING_ALTERNATIVE);
			
			if(next ==null){//current is the last child of parent, looking up parent's next sibling.
				current = current.getParent();
				if(current == null || current == root){// reach the end...
					return null;
				}
			}
		}
		return next;
	}
	
	protected Entry<T> predicate(Entry<T> entry, Alternative<T> alt){
		if(entry==null)return null;
		return predicate.apply(entry.getValue()) ? 
				entry : alt.get(entry);
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 * @param <T>
	 */
	private interface Alternative<T> {
		Entry<T> get(Entry<T> current);
	}
	
	private Alternative<T> CHILD_ALTERNATIVE = new Alternative<T>(){
		public Entry<T> get(Entry<T> current) {
			return null;
		}
	};
	
	private Alternative<T> NEXT_SIBLING_ALTERNATIVE = new Alternative<T>(){
		public Entry<T> get(Entry<T> current) {
			return (current==null)? 
					null : current.getNextSibling();
		}
	};
	
}


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <T>
 */
class DetailIterator<T> extends TraverseIterator<T>{
	private final Iterator<Entry<T>> masterItor;
	private Iterator<Entry<T>> sub;
	
	public DetailIterator(Iterator<Entry<T>> masterItor, Predicate<T> predicate) {
		super(predicate);
		this.masterItor = masterItor;
	}

	protected Entry<T> seekNext(){
		while(sub==null || !sub.hasNext()){ 
			
			if(!masterItor.hasNext())return null;
			sub = initSub(masterItor.next());
			if(sub.hasNext()) break;
		}
		return sub.next();
	}
	
	protected Iterator<Entry<T>> initSub(final Entry<T> root){
		return new TraverseIterator<T>(root, predicate); 
	}
	public void remove() {
		throw new UnsupportedOperationException("this is an immutable itertator");
	}
}//end of class...

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <T>
 */
class NestedSelectorIterator<T> extends DetailIterator<T>{
		private final String selector;
		public NestedSelectorIterator(Iterator<Entry<T>> origin, String selector) {
			super(origin, Predicate.NULL);
			this.selector = selector;
		}
		protected Iterator<Entry<T>> initSub(Entry<T> root){
			return Selectors.iterator(root, selector) ;
		}
}//end of class...


