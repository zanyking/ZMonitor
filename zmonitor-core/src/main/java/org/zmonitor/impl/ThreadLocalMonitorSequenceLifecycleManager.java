
package org.zmonitor.impl;

import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ThreadLocalMonitorSequenceLifecycleManager implements
		MonitorSequenceLifecycleManager {
	private static final ThreadLocal<MonitorSequenceLifecycle> LIFECYCLE_REF = new ThreadLocal<MonitorSequenceLifecycle>();

	public MonitorSequenceLifecycle getLifecycle() {
		MonitorSequenceLifecycle lifecycle = LIFECYCLE_REF.get(); 
		if(lifecycle==null){
			LIFECYCLE_REF.set(lifecycle = new SimpleMonitorSequenceLifecycle());
		}
		//TODO: add shutdown hook here to resolve the termination problem.
		return lifecycle;
	}

}
