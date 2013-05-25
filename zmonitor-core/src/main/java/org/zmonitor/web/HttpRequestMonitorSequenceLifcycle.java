/**HttpRequestTimelineLifcycle.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.SimpleMonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycle;

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
	public HttpRequestMonitorSequenceLifcycle(String oriReqUrl) {
		super();
		
		WebConfigurator webConf = 
			ZMonitorManager.getInstance().getBeanIfAny(
					WebConfigurator.class);
		if(webConf != null)
			urlAccepted = webConf.shouldAccept(oriReqUrl);
	}
	
	public boolean shouldMonitor(TrackingContext trackingCtx) {
		if(urlAccepted){
			return true;
			//TODO: should we check other conditions to get fine grained control?
		}
		return false;
	}
	
}
