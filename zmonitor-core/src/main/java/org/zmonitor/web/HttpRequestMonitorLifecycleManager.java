/**
 * 
 */
package org.zmonitor.web;

import javax.servlet.http.HttpServletRequest;

import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycleManager;

/**
 * 
 *manage the construction and destruction of MonitorSequenceLifecycle in 
 * Java Servlet environment.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class HttpRequestMonitorLifecycleManager  implements MonitorLifecycleManager{

	private static final String KEY_REQ_MSL = "KEY_REQ_MSL";
	private final ThreadLocalMonitorLifecycleManager thlTManager = 
			new ThreadLocalMonitorLifecycleManager();
		
	
	public MonitorLifecycle getLifecycle() {
		HttpRequestContext ctx = HttpRequestContexts.get();
		
		MonitorLifecycle lfcycle = null;
		if(ctx==null){
			//this is not a Servlet thread, but a thread created by some part of a Java Web application.
			lfcycle = thlTManager.getLifecycle();
		}else{
			lfcycle = (HttpRequestMonitorSequenceLifcycle) ctx.getRequest().getAttribute(KEY_REQ_MSL);	
		}
		
		return lfcycle;
	}
	
	public void initLifeCycle(HttpServletRequest req){
		req.setAttribute(KEY_REQ_MSL, 
			new HttpRequestMonitorSequenceLifcycle(
					req.getRequestURL().toString()));
	}
	
	public void disposeLifeCycle(HttpServletRequest req){
		req.removeAttribute(KEY_REQ_MSL);
	}

}
