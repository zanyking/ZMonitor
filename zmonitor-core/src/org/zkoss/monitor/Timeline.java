/*
 * 
 */
package org.zkoss.monitor;

import java.io.Serializable;

import org.zkoss.monitor.spi.Name;


/**
 * A request scope context object focused on collecting time stamps of the life time of this request.<br>
 *  
 * @author Ian YT Tsai(Zanyking)
 */
public class Timeline implements Serializable{
	
	private static final long serialVersionUID = -9139648113614382065L;
	
	protected int counter;
	protected int currentDepth;
	protected MeasurePoint root;
	protected MeasurePoint current;
	protected long selfSpendNanosecond;
	
	/**
	 * Default constructor for inheritance.
	 */
	public Timeline(){
	}
	/**
	 * 
	 * @return the acumulated nanosecond that zmonitor spent to get this timeline.
	 */
	public long getSelfSpendNanosecond(){
		return selfSpendNanosecond;
	}
	/**
	 * 
	 * @param nanosec the execution time that a measure point collective process used. 
	 * @return accumulate nanosecond.
	 */
	public long accumulateSelfSpendNanosec(long nanosec){
		selfSpendNanosecond += nanosec;
		return selfSpendNanosecond;
	}
	
	public MeasurePoint getRoot() {
		return root;
	}

	public boolean isStarted(){
		return root!=null;
	}
	public boolean isFinished(){
		return current == null;
	}
	
	public Name getName() {
		return root==null? null : root.name;
	}
	
	public MeasurePoint getCurrent() {
		return current;
	}

	public int getCounter() {
		return counter;
	}
	public int getRecordAmount() {
		return counter;
	}
	/*package*/int increament(){
		return counter++;
	}
	public int getCurrentDepth() {
		return currentDepth;
	}
	/**
	 * 
	 * @return
	 */
	public long getVeryEndCreateTime(){
		long veryEndCreateTime = MeasurePoint.getVeryEnd(current).createTime;
		return veryEndCreateTime;
	}
	
	private MeasurePoint newInstance(Name name, String mesg, MeasurePoint parent, boolean isLeaf, long createTime){
		return new MeasurePoint(name, mesg, parent, isLeaf, this, createTime);
	}
	
	/**
	 * 
	 * @param name
	 * @param mesg
	 */
	public MeasurePoint start(Name name, String mesg, long createMillis){
		if(root==null){
			current = root = newInstance(name, mesg, null, false, createMillis);
		}else{
			current = newInstance(name, mesg, current, false, createMillis);
		}
		currentDepth++;
		return current;
	}

	/**
	 * if {@link Timeline} has not started before, start it, otherwise, add a simple leaf {@link MeasurePoint} as current stack level's child.   
	 * @param name the name of the new {@link MeasurePoint}
	 * @param mesg the message of the new {@link MeasurePoint}
	 * @return the new {@link MeasurePoint}
	 */
	public MeasurePoint record(Name name, String mesg, long createMillis){
		MeasurePoint rec = newInstance(name, mesg, current, true, createMillis);
		return rec;
	}
	/**
	 * Give an end {@link MeasurePoint} to current stack level then pop it out. 
	 * 
	 * @param name the name of the new {@link MeasurePoint}
	 * @param mesg the message of the new {@link MeasurePoint}
	 * @return the end {@link MeasurePoint}
	 * @throws IllegalStateException if {@link Timeline}) is already ended.
	 */
	public MeasurePoint end(Name name, String mesg){
		return end(name, mesg, System.currentTimeMillis());
	}
	public MeasurePoint end(Name name, String mesg, long createTime){
		if(current==null){
			throw new IllegalStateException("you already ended this timeline and want to end it again?");
		}
		if(current.createTime > createTime){
			throw new IllegalArgumentException("try to tag a measure point which create time is smaller than start stack.");
		}
		currentDepth--;
		MeasurePoint rec = newInstance(name, mesg, current, true, createTime);
		current.markFinalEnd(rec.createTime);
		
		current = current.parent;
		return rec;
	}
	
}
