/**HttpRequestTimelineLifcycle.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import javax.servlet.http.HttpServletRequest;

import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.SimpleMonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycleManager;

/**
 * A HttpRequest life long {@link MonitorLifecycle}.
 *   
 * @author Ian YT Tsai(Zanyking)
 */
public class HttpRequestMonitorSequenceLifcycle extends SimpleMonitorLifecycle {
	private boolean urlAccepted = true;
	/**
	 * The lifetime of this {@link MonitorLifecycle} is as long as a request.
	 * @param requestCtx
	 */
	public HttpRequestMonitorSequenceLifcycle(MonitorLifecycleManager lfcManager, 
			HttpServletRequest req) {
		super(lfcManager);
		
		WebConfigurator webConf = 
			ZMonitorManager.getInstance().getBeanIfAny(
					WebConfigurator.class);
		if(webConf != null)
			urlAccepted = webConf.shouldAccept(req);
	}
	
	public boolean shouldMonitor(TrackingContext trackingCtx) {
		if(urlAccepted){
			return true;
			//TODO: should we check other conditions to get fine grained control?
		}
		return false;
	}
	
}
