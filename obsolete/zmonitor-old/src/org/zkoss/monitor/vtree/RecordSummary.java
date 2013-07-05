/**Measurable.java
 * 2011/3/10
 * 
 */
package org.zkoss.monitor.vtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class RecordSummary<T> {
	@SuppressWarnings("rawtypes")
	private static final Rotate EMPTY_MESGS = new Rotate(0);
	@SuppressWarnings("unchecked")
	private Rotate<T> mesgs = EMPTY_MESGS;
	private int counter;
	private long beforePeriodSum;
	private long afterPeriodSum;
	
	public RecordSummary(){}
	
	public RecordSummary(int mesgSize){
		setMesgSize(mesgSize);
	}
	
	public void setMesgSize(int mesgSize){
		if(mesgSize<0)
			throw new IllegalArgumentException("mesg size cannot < 0! mesgSize:"+mesgSize);
		
		if(mesgSize<mesgs.length())
			throw new IllegalArgumentException("new mesgSize must >= original one! old:new = "+mesgs.length()+":"+mesgSize);
		
		if(mesgs.length()==0)
			mesgs = new Rotate<T>(mesgSize);
		else{
			mesgs = mesgs.startNew(mesgSize);
		}
	}
	
	public int getMesgSize(){
		return mesgs.length();
	}
	
	public void accumulate(long beforePeriod, long afterPeriod){
		counter++;
		beforePeriodSum+=beforePeriod;
		afterPeriodSum+=afterPeriod;
	}
	public int getCounter() {
		return counter;
	}

	public long getBeforePeriodSum() {
		return beforePeriodSum;
	}
	
	public long getAvgBeforePeriod() {
		return beforePeriodSum/counter;
	}
	
	public long getAfterPeriodSum() {
		return afterPeriodSum;
	}
	
	public long getAvgAfterPeriod() {
		return afterPeriodSum/counter;
	}
	
	public T getLast() {
		return mesgs.getLast();
	}
	public List<T> getAll() {
		return new ArrayList<T>(mesgs.getAll());
	}
	public void append(T mesg){
		mesgs.push(mesg);
	}
	/**
	 * 
	 * @author Ian YT Tsai
	 *
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	private static class Rotate<T>{
		@SuppressWarnings("rawtypes")
		private static final List EMPTY = Collections.emptyList();
		private final Object[] arr;
		private int idx = -1;
		private Rotate(int size){
			arr = new Object[size];
		}
		private Rotate(Object[] arr, int idx){
			this.arr = arr;
			this.idx = idx;
		}
		
		public Rotate<T> startNew(int mesgSize) {
			if(mesgSize<arr.length)
				throw new IllegalArgumentException("new Rotate's capacity must >= original one");
			
			Object[] newArr = new Object[mesgSize];
			System.arraycopy(arr, 0, newArr, 0, arr.length);
			Rotate<T> newRotate = new Rotate<T>(newArr, idx);
			return newRotate;
		}

		public void push(T obj){
			if(arr.length==0)return;
			arr[toNext()] = obj;
		}
		
		public T getLast(){
			if(arr.length==0||idx==-1)return null;
			return (T) arr[idx];
		}
		
		public List<T> getAll(){
			if(arr.length==0)return EMPTY;
			return (List<T>) Arrays.asList(arr);
		}
		
		private int toNext(){
			idx++;
			if(idx==arr.length){
				idx = 0;
			}
			return idx;
		}
		public int length(){
			return arr.length;
		}
		
	}//end of class...
}
