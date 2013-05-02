/**HttpRequestTimelineLifcycle.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.SimpleMonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.Name;

/**
 * A HttpRequest life long {@link MonitorSequenceLifecycle}.
 *   
 * @author Ian YT Tsai(Zanyking)
 */
public class HttpRequestMonitorSequenceLifcycle extends SimpleMonitorSequenceLifecycle {
	private boolean urlAccepted = true;
	/**
	 * The lifetime of this {@link MonitorSequenceLifecycle} is as long as a request.
	 * @param requestCtx
	 */
	public HttpRequestMonitorSequenceLifcycle(String oriReqUrl) {
		super();
		
		JavaWebConfCustom webProfilingConf = 
			ZMonitorManager.getInstance().getBeanIfAny(
					JavaWebConfCustom.class);
		
		
		if(webProfilingConf != null)
			urlAccepted = webProfilingConf.shouldAccept(oriReqUrl);
	}
	
	
	public boolean shouldMeasure(Name name, String mesg, long createMillis) {
		if(urlAccepted){
			return true;
			//TODO: should we check other conditions to get fine grained control?
		}
		return false;
	}
	
}
