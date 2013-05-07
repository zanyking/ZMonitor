
package org.zmonitor.impl;

import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycleManager;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ThreadLocalMonitorLifecycleManager implements
		MonitorLifecycleManager {
	private static final ThreadLocal<MonitorLifecycle> LIFECYCLE_REF = new ThreadLocal<MonitorLifecycle>();

	public MonitorLifecycle getLifecycle() {
		MonitorLifecycle lifecycle = LIFECYCLE_REF.get(); 
		if(lifecycle==null){
			LIFECYCLE_REF.set(lifecycle = new SimpleMonitorLifecycle());
		}
		
		return lifecycle;
	}

}
