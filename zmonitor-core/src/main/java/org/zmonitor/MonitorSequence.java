/*
 * 
 */
package org.zmonitor;

import java.io.Serializable;


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
	
	protected long selfSpendNanos;
	protected long selfSpendMillis;
	
	/**
	 * Default constructor for inheritance.
	 */
	public MonitorSequence(){}
	/**
	 * 
	 * @return the acumulated nanosecond that zmonitor spent to get this Measure Sequence.
	 */
	public long getSelfSpendNanos(){
		return selfSpendNanos;
	}
	/**
	 * 
	 * @return
	 */
	public long getSelfSpendMillis(){
		return selfSpendMillis;
	}
	/**
	 * 
	 * @param nanosec the execution time that a measure point collective process used. 
	 */
	public void accumulateSelfSpend(long nanosec, long millis){
		selfSpendNanos += nanosec;
		selfSpendMillis += millis;
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

	public Class getRootTrackerClass() throws ClassNotFoundException{
		if(!isStarted())
			throw new IllegalStateException("hasn't started yet.");
		MonitorMeta meta = root.getMonitorMeta();
		return Class.forName(meta.getClassName());
	}
	
	/**
	 * 
	 * @param name
	 * @param mesg
	 */
	public MonitorPoint start(TrackingContext trackingCtx){
		if(root==null){
			current = root = trackingCtx.create(null);
		}else{
			current = trackingCtx.create(current);
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
	public MonitorPoint record(TrackingContext trackingCtx){
		MonitorPoint rec = trackingCtx.create(current);
		return rec;
	}
	/**
	 * <pre>
	 * 	|-mp		-> START (createMillis)
	 * 		|-mp
	 *		|-mp				
	 *		|-mp	-> END (createMillis)
	 *</pre>
	 * @param name
	 * @param mesg
	 * @param createMillis
	 * @return a new monitor point which is an end of current monitor point. 
	 */
	public MonitorPoint end(TrackingContext trackingCtx){
		if(current==null){
			//TODO how about return null?
			throw new IllegalStateException("you already ended this monitor Sequence and want to end it again?");
		}
		if(current.getCreateMillis() > trackingCtx.getCreateMillis()){
			throw new IllegalArgumentException("try to tag a monitor point which create time is smaller than start stack.");
		}
		currentDepth--;
		MonitorPoint endMP = trackingCtx.create(current);
		
		current = current.getParent();
		return endMP;
	}
	
}
