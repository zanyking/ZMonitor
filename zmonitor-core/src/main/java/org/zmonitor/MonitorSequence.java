/*
 * 
 */
package org.zmonitor;

import java.io.Serializable;

import org.zmonitor.spi.Name;


/**
 * Monitor Sequence is a data object which collect MeasurePoints during an execution.<br>
 * 
 * 
 *  scope context object focused on collecting time stamps of the life time of a request.<br>
 *  
 * @author Ian YT Tsai (Zanyking)
 * @see MonitorPoint
 */
public class MonitorSequence implements Serializable{
	
	private static final long serialVersionUID = -9139648113614382065L;
	
	
	
	protected MonitorPoint root;
	
	//TODO: state object while recording, should be moved to life-cycle.
	protected MonitorPoint current;
	//TODO: cached value of current mp depth, 
	protected int currentDepth;
	//TODO: an extra information that can be recalculated by counting mp tree.
	protected int counter;
	
	protected long selfSpendNanosecond;
	
	/**
	 * Default constructor for inheritance.
	 */
	public MonitorSequence(){}
	/**
	 * 
	 * @return the acumulated nanosecond that zmonitor spent to get this Measure Sequence.
	 */
	public long getSelfSpendNanos(){
		return selfSpendNanosecond;
	}
	/**
	 * 
	 * @param nanosec the execution time that a measure point collective process used. 
	 * @return accumulate nanosecond.
	 */
	public long accumulateSelfSpendNanos(long nanosec){
		selfSpendNanosecond += nanosec;
		return selfSpendNanosecond;
	}
	
	public MonitorPoint getRoot() {
		return root;
	}

	public boolean isStarted(){
		return root!=null;
	}
	public boolean isFinished(){
		return current == null;
	}
	
	public Name getName() {
		return root==null? null : root.getName();
	}
	
	public MonitorPoint getCurrent() {
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
	public long getVeryEndCreateMillis(){
		long veryEndCreateTime = getVeryEnd(current).getCreateMillis();
		return veryEndCreateTime;
	}
	/**
	 * 
	 * @param current
	 * @return
	 */
	private static MonitorPoint getVeryEnd(MonitorPoint current){
		MonitorPoint lastChild = current.getLastChild();
		return (lastChild==null)? current : getVeryEnd(lastChild);
	}
	
	private MonitorPoint newInstance(Name name, String mesg, MonitorPoint parent, boolean isLeaf, long createMillis){
		return new MonitorPoint(name, mesg, parent, isLeaf, this, createMillis);
	}
	
	/**
	 * 
	 * @param name
	 * @param mesg
	 */
	public MonitorPoint start(Name name, String mesg, long createMillis){
		if(root==null){
			current = root = newInstance(name, mesg, null, false, createMillis);
		}else{
			current = newInstance(name, mesg, current, false, createMillis);
		}
		currentDepth++;
		return current;
	}

	/**
	 * if {@link MonitorSequence} has not started before, start it, otherwise, add a simple leaf {@link MonitorPoint} as current stack level's child.   
	 * @param name the name of the new {@link MonitorPoint}
	 * @param mesg the message of the new {@link MonitorPoint}
	 * @return the new {@link MonitorPoint}
	 */
	public MonitorPoint record(Name name, String mesg, long createMillis){
		MonitorPoint rec = newInstance(name, mesg, current, true, createMillis);
		return rec;
	}
	/**
	 * Using a {@link MonitorPoint} as an end of current ms-mp-stack, 
	 * a pop operation will be performed immediately after tracking. 
	 * 
	 * @param name the name of the new {@link MonitorPoint}
	 * @param mesg the message of the new {@link MonitorPoint}
	 * @return the end {@link MonitorPoint}
	 * @throws IllegalStateException if {@link MonitorSequence}) is already ended.
	 */
	public MonitorPoint end(Name name, String mesg){
		return end(name, mesg, System.currentTimeMillis());
	}
	/**
	 * 	|-mp		-> START (createMillis)
	 * 		|-mp
	 *		|-mp				
	 *		|-mp	-> END (createMillis)
	 *
	 * @param name
	 * @param mesg
	 * @param createMillis
	 * @return a new monitor point which is an end of current monitor point. 
	 */
	public MonitorPoint end(Name name, String mesg, long createMillis){
		if(current==null){
			//TODO how about return null?
			throw new IllegalStateException("you already ended this monitor Sequence and want to end it again?");
		}
		if(current.getCreateMillis() > createMillis){
			throw new IllegalArgumentException("try to tag a monitor point which create time is smaller than start stack.");
		}
		currentDepth--;
		MonitorPoint endMP = newInstance(name, mesg, current, true, createMillis);
		current.markLastMillis(endMP.getCreateMillis());
		
		current = current.getParent();
		return endMP;
	}
	
}
