/**
 * 2011/4/6
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.ZMonitor;
import org.zmonitor.MonitorSequence;
import org.zmonitor.spi.MonitorLifecycle;
/**
 * 
 * the {@link ZMonitor} implementation requires a manager to get a {@link MonitorLifecycle} 
 * which is the keeper of {@link MonitorSequence}.<br>
 * 
 * <p>
 * Every application which has special requirements needs to implement it's own Manager, 
 * and set it back to ZMonitorManager while application initializing.
 * </p>
 * 
 * <p>
 * The implementation of the manager normally based on Threadlocal to keep the reference to {@link MonitorLifecycle} to preserve the continuation. 
 * which will be retrieved by monitoring point in the thread the application created.
 * </p>
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorLifecycleManager {
	 
	/**
	 * could be called by logger adaptor implementation in any place of the application.<br>
	 * @return
	 */
	public MonitorLifecycle getLifecycle();
	/**
	 * 
	 * @param lfc
	 */
	public void disposeLifecycle(MonitorLifecycle lfc);
	
}
