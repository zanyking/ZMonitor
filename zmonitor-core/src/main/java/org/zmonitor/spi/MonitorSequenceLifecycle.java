/**TimelineLifcycle.java
 * 2011/3/29
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.MonitorSequence;


/**
 * <p>
 * Used to manage the life-cycle of a {@link MonitorSequence} instance.
 * The entire profiler framework should always depends on the 
 * implementation of this interface to retrieve a {@link MonitorSequence} instance.<br>
 * 
 * </p>
 * 
 * @author Ian YT Tsai(Zanyking)
 * @see org.zmonitor.impl.SimpleMonitorSequenceLifecycle
 * @see org.zmonitor.web.HttpRequestTimelineLifcycle
 */
public interface MonitorSequenceLifecycle {
	/**
	 * Initialize a MonitorSequence instance if not exist, or just return the existing one.<br>
	 * The first time to call this method will initialize a new MonitorSequence lifecycle. 
	 * @return a MonitorSequence instance, shouldn't return null.
	 */
	public MonitorSequence getInstance();
	/**
	 * 
	 * @return the current Timeline, could be null if {@link #getInstance()} 
	 * hasn't been called before.  
	 */
	public MonitorSequence getMonitorSequence();
	/**
	 * 
	 * @return true if the Timeline life-cycle is initialized, false otherwise.
	 */
	public boolean isInitialized();
	/**
	 * if {@link #isInitialized()} is false, this method will always return false.<br>
	 * if {@link #isInitialized()} is true, this method will test if current MeasureSequence was started before.<br>
	 * @return true test if current MeasureSequence was started, false otherwise.
	 */
	public boolean isZMonitorStarted();
	/**
	 * finish current Measure Sequence life-cycle, clear state.  
	 */
	public void flush();
	/**
	 * put something into life-cycle.
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value);
	/**
	 * get something from life-cycle.
	 * @param <T>
	 * @param key
	 * @return
	 */
	public<T> T getAttribute(String key);
	
	/**
	 * Internal use, implement this method to tell measuring mechanism to measure or not. 
	 * @param name
	 * @param mesg
	 * @param createMillis
	 * @return
	 */
	public boolean shouldMeasure(Name name, String mesg, long createMillis);

}
