/**Configurator.java
 * 2011/4/2
 * 
 */
package org.zkoss.monitor;

import org.zkoss.monitor.impl.ThreadLocalTimelineLifecycleManager;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.Configurator;
import org.zkoss.monitor.spi.TimelineLifecycleManager;




/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Ignitor {
	private static volatile ZMonitorManager sZMonitorManager;
	
	private static boolean ignited = false;
	
	static ZMonitorManager getInstance(){
		return sZMonitorManager;
	}
	/**
	 * 
	 * @return
	 */
	public static synchronized boolean isIgnited(){
		return ignited;
	}
	/**
	 * Use {@link ThreadLocalTimelineLifecycleManager} as default @{link TimelineLifecycleManager}.
	 * @param configurator
	 * @return
	 */
	public static boolean ignite( Configurator configurator){
		return ignite(new ThreadLocalTimelineLifecycleManager(), configurator);
	}
	/**
	 * 
	 * @param lifecycleManager
	 * @param configurator
	 * @return has lifecycle control or not.
	 */
	public static synchronized boolean ignite(TimelineLifecycleManager lifecycleManager, Configurator configurator){
		return ignite(new ZMonitorManager(), lifecycleManager, configurator);
	}
	/**
	 * 
	 * @param manager
	 * @param lifecycleManager
	 * @param configurator
	 * @return
	 */
	public static synchronized boolean ignite(ZMonitorManager manager, TimelineLifecycleManager lifecycleManager, Configurator configurator){
		if(manager==null)
			throw new IllegalArgumentException("ZMonitorManager cannot be null!");
		ZMLog.info("ZMonitor Ignition START... ");// will print out in any case, since there's no customized ZKLog here yet!
		if(configurator==null)
			throw new IllegalArgumentException("Configurator cannot be null!");
		if(lifecycleManager==null)
			throw new IllegalArgumentException("TimelineLifecycleManager cannot be null!");
		
		if(ignited) return false;
		ignited = true;
		
//		if(sZMonitorManager!=null)return false;
		manager.setLifecycleManager(lifecycleManager);
		configurator.configure(manager);
		sZMonitorManager = manager;
		
		try {
			Class.forName("org.zkoss.monitor.logger.log4j.Driver");
		} catch (Throwable e) {
			ZMLog.info("log4j is not applicable in this environment: " +
					e.getClass()+" : "+e.getMessage());
		}
		return true;
	}
	/**
	 * Destroy the current class loader's ZMonitorManager instance.<br>
	 * Caller need to guarantee it has enough knowledge of the ZMonitor's ClassLoader lifecycle management, 
	 * otherwise the invocation of this method might introduce serious data inconsistency of the ongoing ZMonitor timeline handling process.
	 */
	public static void destroy(){
		ZMonitorManager m = sZMonitorManager;
		synchronized (Ignitor.class){
			if(sZMonitorManager!=m)return;
			m.destroy();
			sZMonitorManager = null;
			ignited = false;	
		}
		
	}
	
}
