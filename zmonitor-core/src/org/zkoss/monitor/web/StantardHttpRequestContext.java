/**StantradHttpRequestContext.java
 * 2011/4/1
 * 
 */
package org.zkoss.monitor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.monitor.spi.TimelineLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class StantardHttpRequestContext implements HttpRequestContext {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String requestURL;
	private HttpRequestTimelineLifcycle lifecycle;
	
	/**
	 * 
	 */
	public boolean isInitialized() {
		return request!=null;
	}
	/**
	 * 
	 * @param req
	 * @param res
	 * @return true, if this request should trigger the profiling mechanism, false otherwise. 
	 */
	public void init(HttpServletRequest req, HttpServletResponse res){
		if(req==null || res==null)
			throw new IllegalArgumentException("can't be null= "+req+" : "+res);
		
		String requestURL = req.getRequestURL().toString();
		this.requestURL = requestURL;
		this.request = req;
		this.response = res;
		lifecycle = new HttpRequestTimelineLifcycle(this);
	}
	/**
	 * clear the context from Servlet Thread.
	 */
	public void dispose(){
		request = null;
		response = null;
	}
	/**
	 * 
	 * @return current {@link HttpServletRequest} on Servlet Thread.
	 */
	public HttpServletRequest getRequest(){
		return request;
	}
	/**
	 * 
	 * @return current {@link HttpServletResponse} on Servlet Thread.
	 */
	public HttpServletResponse getResponse(){
		return response;
	}
	/**
	 * 
	 * @return current {@link HttpSession} on Servlet Thread.
	 */
	public HttpSession getSession(){
		return request.getSession();
	}
	/**
	 * a convenient method to let you get back the original URL if a request has been forwarded before.
	 * @return the original Request URL 
	 */
	public String getOriginalRequestURL(){
		return  requestURL;
	}
	
	public TimelineLifecycle getTimelineLifecycle() {
		return lifecycle;
	}

}
