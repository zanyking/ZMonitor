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
	public void disposeLifecycle(MonitorLifecycle lfc) {
		HttpRequestContext ctx = HttpRequestContexts.get();
		if(ctx==null){
			thlTManager.disposeLifecycle(lfc);
		}else{
			ctx.getRequest().removeAttribute(KEY_REQ_MSL);
		}
	}

	public void initRequest(HttpServletRequest req){
		req.setAttribute(KEY_REQ_MSL, 
			new HttpRequestMonitorSequenceLifcycle(
					this, req));
	}
	
	public void finishRequestIfAny(HttpServletRequest req){
		MonitorLifecycle lfcycle = (MonitorLifecycle) req.getAttribute(KEY_REQ_MSL);
		if(lfcycle!=null && 
				!lfcycle.isFinished() && 
				lfcycle.isMonitorStarted()){
			// force terminate the current MonitorLifecycle if any.
			// normally, the MonitorLifecycle should be ended by previous pop, but if developer 
			// doesn't operate the stack properly, then this fail safe mechanism will be triggered.
			lfcycle.finish();
		}
		req.removeAttribute(KEY_REQ_MSL);
	}

}
