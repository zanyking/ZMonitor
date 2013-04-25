/**Requests.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class HttpRequestContexts {
	
	private static final ThreadLocal<HttpRequestContext> HTTP_REQ_CTX_REF = new ThreadLocal<HttpRequestContext>();
	
	/**
	 * 
	 * @param context
	 * @param req
	 * @param res
	 */
	public static void init(HttpRequestContext context, HttpServletRequest req, HttpServletResponse res){
		HTTP_REQ_CTX_REF.set(context);
		context.init(req, res);
	}
	/**
	 * 
	 * @return
	 */
	public static HttpRequestContext get(){
		return HTTP_REQ_CTX_REF.get();
	}
	/**
	 * 
	 */
	public static void dispose(){
		get().dispose();
		HTTP_REQ_CTX_REF.remove();
	}
	/**
	 * 
	 * @return
	 */
	public static MonitorSequenceLifecycleManager getMSLifecycleManager(){
		return new MonitorSequenceLifecycleManager() {
			private final ThreadLocalMonitorSequenceLifecycleManager thlTManager = 
				new ThreadLocalMonitorSequenceLifecycleManager();
			
			public MonitorSequenceLifecycle getLifecycle() {
				if(get()==null){
					//this is not a Servlet thread, but a thread created by some part of a Java Web application.
					return thlTManager.getLifecycle();	
				}else{
					return get().getTimelineLifecycle();
				}
			}
		};
	}
	
}
