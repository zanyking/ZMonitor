/**HttpRequestTimelineLifcycle.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.SimpleMonitorSequenceLifecycle;
import org.zmonitor.spi.Name;

/**
 * A HttpRequest life long Timeline Lifecycle.
 *   
 * @author Ian YT Tsai(Zanyking)
 */
public class HttpRequestTimelineLifcycle extends SimpleMonitorSequenceLifecycle {
	private boolean urlAccepted = true;
	/**
	 * The lifetime of this TimelineLifecycle is as long as a request.
	 * @param requestCtx
	 */
	public HttpRequestTimelineLifcycle(String oriReqUrl) {
		super();
		
		JavaWebConfiguration webProfilingConf = 
			ZMonitorManager.getInstance().getBeanIfAny(
					JavaWebConfiguration.class);
		
		
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
