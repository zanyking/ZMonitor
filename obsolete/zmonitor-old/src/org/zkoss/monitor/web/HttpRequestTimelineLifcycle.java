/**HttpRequestTimelineLifcycle.java
 * 2011/4/1
 * 
 */
package org.zkoss.monitor.web;

import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.impl.SimpleTimelineLifecycle;
import org.zkoss.monitor.spi.Name;

/**
 * A HttpRequest life long Timeline Lifecycle.
 *   
 * @author Ian YT Tsai(Zanyking)
 */
public class HttpRequestTimelineLifcycle extends SimpleTimelineLifecycle {
	private final HttpRequestContext requestCtx;
	private boolean urlAccepted = true;
	/**
	 * The lifetime of this TimelineLifecycle is as long as a request.
	 * @param requestCtx
	 */
	public HttpRequestTimelineLifcycle(HttpRequestContext requestCtx) {
		super();
		this.requestCtx = requestCtx;
		JavaWebConfiguration webProfilingConf = 
			ZMonitorManager.getInstance().getCustomConfiguration(
					JavaWebConfiguration.class);
		
		if(webProfilingConf != null)
			urlAccepted = webProfilingConf.shouldAccept(
					requestCtx.getOriginalRequestURL());
	}
	
	
	public boolean shouldMeasure(Name name, String mesg, long createMillis) {
		if(urlAccepted){
			return true;//TODO: should also check other conditions to get fine grained control.
		}
		return false;
	}
	
}
