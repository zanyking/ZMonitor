/**Ignitor.java
 * 2011/4/2
 * 
 */
package org.zmonitor;

import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;




/**
 * 
 * TODO this class should be merged into ZMonitorManager, 
 * Ignitor is a helper that shouldn't be exposed to user.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Ignitor {
	private static volatile ZMonitorManager sZMonitorManager;
	
	private static boolean ignited = false;
	
//	static ZMonitorManager getInstance(){
//		return sZMonitorManager;
//	}
//	/**
//	 * 
//	 * @return
//	 */
//	public static synchronized boolean isIgnited(){
//		return ignited;
//	}
//	/**
//	 * Use {@link ThreadLocalMonitorSequenceLifecycleManager} as default @{link TimelineLifecycleManager}.
//	 * @param configurator
//	 * @return
//	 */
//	public static boolean ignite( Configurator configurator){
//		return ignite(new ThreadLocalMonitorSequenceLifecycleManager(), configurator);
//	}
//	/**
//	 * 
//	 * @param lifecycleManager
//	 * @param configurator
//	 * @return has lifecycle control or not.
//	 */
//	public static synchronized boolean ignite(MonitorSequenceLifecycleManager lifecycleManager, 
//			Configurator configurator){
//		return ignite(new ZMonitorManager(), lifecycleManager, configurator);
//	}
//	/**
//	 * 
//	 * @param manager
//	 * @param lifecycleManager
//	 * @param configurator
//	 * @return
//	 */
//	public static synchronized boolean ignite(ZMonitorManager manager, 
//			MonitorSequenceLifecycleManager lifecycleManager, Configurator configurator){
//		if(manager==null)
//			throw new IllegalArgumentException("ZMonitorManager cannot be null!");
//		ZMLog.info("ZMonitor Ignition START... ");// will print out in any case, since there's no customized ZKLog here yet!
//		if(configurator==null)
//			throw new IllegalArgumentException("Configurator cannot be null!");
//		if(lifecycleManager==null)
//			throw new IllegalArgumentException("TimelineLifecycleManager cannot be null!");
//		
//		if(ignited) return false;
//		ignited = true;
//		
////		if(sZMonitorManager!=null)return false;
//		manager.setLifecycleManager(lifecycleManager);
//		XMLConfiguration ctxt = null;//TODO: construct a proper ConfigContext
//		configurator.configure(manager, ctxt);
//		sZMonitorManager = manager;
//		
//		try {//TODO log4j shouldn't pollute this part of code...
//			Class.forName("org.zmonitor.logger.log4j.Driver");
//		} catch (Throwable e) {
//			ZMLog.info("log4j is not applicable in this environment: " +
//					e.getClass()+" : "+e.getMessage());
//		}
//		return true;
//	}
//	/**
//	 * Destroy the current class loader's ZMonitorManager instance.<br>
//	 * Caller need to guarantee it has enough knowledge of the ZMonitor's ClassLoader lifecycle management, 
//	 * otherwise the invocation of this method might introduce serious data inconsistency of the ongoing ZMonitor timeline handling process.
//	 */
//	public static void destroy(){
//		ZMonitorManager m = sZMonitorManager;
//		synchronized (Ignitor.class){
//			if(sZMonitorManager!=m)return;
//			m.stop();
//			sZMonitorManager = null;
//			ignited = false;	
//		}
//		
//	}
	
}
