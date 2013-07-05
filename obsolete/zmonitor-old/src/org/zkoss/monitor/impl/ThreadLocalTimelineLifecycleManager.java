/**ThreadLocalTimelineLifecycleManager.java
 * 2011/4/6
 * 
 */
package org.zkoss.monitor.impl;

import org.zkoss.monitor.spi.TimelineLifecycle;
import org.zkoss.monitor.spi.TimelineLifecycleManager;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ThreadLocalTimelineLifecycleManager implements
		TimelineLifecycleManager {
	private static final ThreadLocal<TimelineLifecycle> LIFECYCLE_REF = new ThreadLocal<TimelineLifecycle>();

	public TimelineLifecycle getLifecycle() {
		TimelineLifecycle lifecycle = LIFECYCLE_REF.get(); 
		if(lifecycle==null){
			LIFECYCLE_REF.set(lifecycle = new SimpleTimelineLifecycle());
		}
		//TODO: add shutdown hook here to resolve the termination problem.
		return lifecycle;
	}

}
