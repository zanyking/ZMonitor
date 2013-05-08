/**
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.zmonitor.spi.Name;




/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class MonitorPoint implements Serializable{
	private static final long serialVersionUID = 1772552143735347953L;
	
	private MonitorSequence mSequence;
	private long createMillis;
	private int stack;
	private int index;
	
	
	private MonitorPoint parent;
	private MonitorPoint previousSibling;
	private MonitorPoint nextSibling;
	
	
	private MonitorPoint firstChild;
	private MonitorPoint lastChild;
	
	
	
	private Name name;
	private String message;
	
	/**
	 * 
	 * @param parent
	 * @param mesg
	 */
	public MonitorPoint(Name name, String mesg, MonitorPoint parent, 
			boolean isLeaf, 
			MonitorSequence mSequence, 
			long createMillis) {
		
		
		this.mSequence = mSequence;
		this.mSequence.increament();
		
		this.createMillis = createMillis;
		this.name = name;
		this.message = mesg;//TODO: has potential to be an object...
		this.parent = parent;
		this.stack = (parent==null) ? 0 : parent.stack+1;
		
		if(parent!=null){
			index = parent.size(); 
			parent.append(this);
		}else{// this is the root mp.
			index = 0;
		}
	}
	/**
	 * 
	 * @return the amount of kids.
	 */
	public int size(){
		if(lastChild==null)return 0;
		return lastChild.getIndex()+1;
	}
	
	/**
	 * 
	 * @param newChild
	 */
	private void append(MonitorPoint newChild){
		if(this.firstChild==null){
			this.firstChild = this.lastChild = newChild;
			return;
		}
		if(this.lastChild.nextSibling!=null)
			throw new IllegalStateException("the lastChild's nextSibling is not null!!!");
		if(newChild.equals(this.lastChild))
			throw new IllegalStateException("try to append lastChild twice!");
		
		this.lastChild.nextSibling = newChild;//reference: old -> new 
		newChild.previousSibling = this.lastChild;//reference: old <- new
		/*
		 *     parent
		 * 			|- lastChildRef
		 * old <=> new 
		 */
		this.lastChild = newChild;
	}
	
	
	public int getIndex() {
		return index;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public Name getName() {
		return name;
	}
	
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}

	public MonitorPoint getParent(){
		return parent;
	}
	
	public long getCreateMillis(){
		return createMillis;
	}
	
	/**
	 * @return
	 */
	public MonitorPoint getPreviousSibling(){
		return previousSibling;
	}
	/**
	 * @return
	 */
	public MonitorPoint getNextSibling() {
		return nextSibling;
	}
	/**
	 * 
	 * @return
	 */
	public MonitorPoint getFirstChild() {
		return firstChild;
	}
	/**
	 * 
	 * @return
	 */
	public MonitorPoint getLastChild(){
		return  lastChild;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isLeaf(){
		return firstChild==null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MonitorPoint> getChildren(){
		return new KidsList();
	}

	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	private class KidsList implements List<MonitorPoint> {
		public int size() {
			return MonitorPoint.this.size();
		}

		public boolean isEmpty() {
			return isLeaf();
		}

		public boolean contains(Object o) {
			if(!(o instanceof MonitorPoint))return false;
			for(MonitorPoint mp=firstChild; mp!=null; mp=mp.getNextSibling()){
				if(mp.equals(o))return true;
			}
			return false;
		}

		public Iterator<MonitorPoint> iterator() {
			return new Iterator<MonitorPoint>(){
				private MonitorPoint cursor = null;
				private MonitorPoint next = firstChild;
				public boolean hasNext() {
					return next!=null;
				}
				public MonitorPoint next() {
					cursor = next;
					next = cursor.getNextSibling();
					return cursor;
				}
				public void remove() {
					throw new UnsupportedOperationException(
							"this is a read only iterator");
				}
			};
		}

		public Object[] toArray() {
			MonitorPoint[] arr = new MonitorPoint[size()];
			int i=0;
			for(MonitorPoint mp=firstChild; mp!=null; mp=mp.getNextSibling()){
				arr[i++] = mp;
			}
			return arr;
		}

		public <T> T[] toArray(T[] a) {
			Object[] arr = new Object[size()];
			int i=0;
			for(MonitorPoint mp=firstChild; mp!=null; mp=mp.getNextSibling()){
				arr[i++] = mp;
			}
			return (T[]) arr;
		}

		public boolean add(MonitorPoint e) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean containsAll(Collection<?> c) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean addAll(Collection<? extends MonitorPoint> c) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean addAll(int index,
				Collection<? extends MonitorPoint> c) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public void clear() {
			throw new UnsupportedOperationException("this is a read-only list");
		}

		public MonitorPoint get(int index) {
			if(index<0 || index>=size())
				throw new IndexOutOfBoundsException("size: "+size()+", idx: "+index);
			MonitorPoint mp;
			if(index+1 < size()-index ){//->
				mp = firstChild;
				for(int k=0; k<index; k++){
					mp = mp.getNextSibling();
				}
			}else{//<-
				mp = lastChild;// size-1
				for(int k=size()-1; k>index; k--){
					mp = mp.getPreviousSibling();
				}
			}
			return mp;
		}

		public MonitorPoint set(int index, MonitorPoint element) {
			throw new UnsupportedOperationException("this is a read-only list");
		}
		public void add(int index, MonitorPoint element) {
			throw new UnsupportedOperationException("this is a read-only list");
		}
		public MonitorPoint remove(int index) {
			throw new UnsupportedOperationException("this is a read-only list");
		}
		public int indexOf(Object o) {
			int i=0;
			for(MonitorPoint mp=firstChild; mp!=null; mp=mp.getNextSibling()){
				if(o.equals(mp))return i;
				i++;
			}
			return -1;
		}

		public int lastIndexOf(Object o) {
			int i=size()-1;
			for(MonitorPoint mp=lastChild; mp!=null; mp=mp.getPreviousSibling()){
				if(o.equals(mp))return i;
				i--;
			}
			return -1;
		}

		public ListIterator<MonitorPoint> listIterator() {
			throw new UnsupportedOperationException("this is a read-only list");
		}
		public ListIterator<MonitorPoint> listIterator(int index) {
			throw new UnsupportedOperationException("this is a read-only list");
		}
		public List<MonitorPoint> subList(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException("this is a read-only list");
		}
	}//end of class...


}
