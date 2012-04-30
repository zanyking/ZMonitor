/**TimelineLifcycle.java
 * 2011/3/29
 * 
 */
package org.zkoss.monitor.spi;

import org.zkoss.monitor.Timeline;


/**
 * <p>
 * Used to manage the life-cycle of a {@link Timeline} instance.
 * The entire profiler framework should always depends on the 
 * implementation of this interface to retrieve a {@link Timeline} instance.<br>
 * 
 * </p>
 * 
 * @author Ian YT Tsai(Zanyking)
 * @see org.zkoss.monitor.impl.SimpleTimelineLifecycle
 * @see org.zkoss.monitor.web.HttpRequestTimelineLifcycle
 */
public interface TimelineLifecycle {
	/**
	 * Initialize a Timeline instance if not exist, or just return the exists one.<br>
	 * The first time to call this method will initialize a new Timeline lifecycle. 
	 * @return a Timeline instance, shouldn't return null.
	 */
	public Timeline getInstance();
	/**
	 * 
	 * @return the current Timeline, could be null if {@link #getInstance()} 
	 * hasn't been called before.  
	 */
	public Timeline getTimeline();
	/**
	 * 
	 * @return true if the Timeline life-cycle is initialized, false otherwise.
	 */
	public boolean isInitialized();
	/**
	 * if {@link #isInitialized()} is false, this method will always return false.<br>
	 * if {@link #isInitialized()} is true, this method will test if current Timeline was started before.<br>
	 * @return true test if current Timeline was started, false otherwise.
	 */
	public boolean hasTimelineStarted();
	/**
	 * finish current Timeline lifecycle, clear state.  
	 */
	public void reset();
	/**
	 * put something into lifecycle.
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value);
	/**
	 * get something from lifecycle.
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
