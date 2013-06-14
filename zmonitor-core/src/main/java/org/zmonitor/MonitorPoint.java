/**
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.zmonitor.util.Arguments;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class MonitorPoint implements Serializable{
	private static final long serialVersionUID = 1772552143735347953L;
	
	// tree node data
	private MonitorPoint parent;
	private int index;
	private MonitorPoint previousSibling;
	private MonitorPoint nextSibling;
	private MonitorPoint firstChild;
	private MonitorPoint lastChild;
	private MonitorSequence mSequence;
	
	// Context info
	private MonitorMeta monitorMeta;
	
	private long createMillis;
	private Object message;
	
	/**
	 * 
	 * @param parent
	 * @param mesg
	 */
	public MonitorPoint( MonitorMeta mm,
			Object mesg, 
			MonitorSequence mSequence, 
			long createMillis) {
		Arguments.checkNotNull(mm, "monitorMeta should never be null, ",
				"please check your TrackingContext usage.");
		this.monitorMeta = mm;
		this.message = mesg;
		this.mSequence = mSequence;
		this.mSequence.increament();
		
		this.createMillis = createMillis;
	}
	
	public MonitorSequence getMonitorSequence(){
		return mSequence;
	}
	
	public void setParent(MonitorPoint parent){
		if(parent==null)
			throw new IllegalStateException("parent cannot be null!");
		
		this.parent = parent;
		
		parent.appendChild(this);
	}
	
	public MonitorMeta getMonitorMeta(){
		return monitorMeta;
	}
	public void appendChild(MonitorPoint newChild){
		if(this.firstChild==null){// this mp has no child recently.
			this.firstChild = this.lastChild = newChild;
			return;
		}
		if(getChildren().contains(newChild))
			throw new IllegalStateException("already contains this child: "+newChild);
		if(this.lastChild.nextSibling!=null)
			throw new IllegalStateException("the lastChild's nextSibling is supposed to be null !!!");
		
		int newIdx = this.size();
		this.lastChild.nextSibling = newChild;//reference: old -> new 
		newChild.previousSibling = this.lastChild;//reference: old <- new
		this.lastChild = newChild;
		this.lastChild.setIndex(newIdx);
	}
	/**
	 * 
	 * @return the amount of kids.
	 */
	public int size(){
		if(lastChild==null)return 0;
		return lastChild.getIndex()+1;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int idx){
		index = idx;
	}

	
	public void setMessage(Object message) {
		this.message = message;
	}
	public Object getMessage() {
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
		return new KidList();
	}
	/**
	 * 
	 * @param mp
	 * @return
	 */
	public boolean isSimilar(MonitorPoint mp){
		MonitorMeta cInfo = mp.getMonitorMeta();
		if(this.monitorMeta==null){
			if(cInfo!=null)return false;
			//both are null.
		} else if(!this.monitorMeta.equals(cInfo)){
			return false;
		}
		
		return this.getIndex() == mp.getIndex();
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	private class KidList implements List<MonitorPoint> {
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

		@SuppressWarnings("unchecked")
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
