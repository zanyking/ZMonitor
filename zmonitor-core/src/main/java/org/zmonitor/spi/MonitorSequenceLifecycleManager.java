/**TimelineLifecycleManager.java
 * 2011/4/6
 * 
 */
package org.zmonitor.spi;


/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorSequenceLifecycleManager {
	//TODO this interface suppose to handle MS in any situation, but it is impossible.
	//
	/**
	 * 
	 * @return
	 */
	public MonitorSequenceLifecycle getLifecycle();
	
}
