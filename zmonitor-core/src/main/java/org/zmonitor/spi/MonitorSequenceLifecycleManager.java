/**
 * 2011/4/6
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.ZMonitor;
import org.zmonitor.MonitorSequence;
import org.zmonitor.spi.MonitorSequenceLifecycle;
/**
 * 
 * the {@link ZMonitor} implementation requires a manager to get a {@link MonitorSequenceLifecycle} 
 * which is the keeper of {@link MonitorSequence}.<br>
 * 
 * <p>
 * Every application which has special requirements needs to implement it's own Manager, 
 * and set it back to ZMonitorManager while application initializing.
 * </p>
 * 
 * <p>
 * The implementation of the manager normally based on Threadlocal to keep the reference to {@link MonitorSequenceLifecycle} to preserve the continuation. 
 * which will be retrieved by monitoring point in the thread the application created.
 * </p>
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorSequenceLifecycleManager {
	 
	/**
	 * could be called by logger in any place of the application.<br>
	 * @return
	 */
	public MonitorSequenceLifecycle getLifecycle();
	
}
