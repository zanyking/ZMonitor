/**HttpRequestContext.java
 * 2011/3/29
 * 
 */
package org.zmonitor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zmonitor.spi.MonitorLifecycle;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public interface HttpRequestContext {

	/**
	 * 
	 * @return if current context object has been initialized.
	 */
	public boolean isInitialized();
	/**
	 * 
	 * @param req
	 * @param res
	 */
	public void init(HttpServletRequest req, HttpServletResponse res);
	/**
	 * clear the context from Servlet Thread.
	 */
	public void dispose();
	/**
	 * 
	 * @return current {@link HttpServletRequest} on Servlet Thread.
	 */
	public HttpServletRequest getRequest();
	/**
	 * 
	 * @return current {@link HttpServletResponse} on Servlet Thread.
	 */
	public HttpServletResponse getResponse();
	/**
	 * 
	 * @return current {@link HttpSession} on Servlet Thread.
	 */
	public HttpSession getSession();
	/**
	 * a convenient method to let you get back the original URL if a request has been forwarded before.
	 * @return the original Request URL 
	 */
	public String getOriginalRequestURL();
//	/**
//	 * TODO: I don't think mix this interface with MonitorSequenceLifecycle is a good idea... 
//	 * @return
//	 */
//	public MonitorSequenceLifecycle getTimelineLifecycle();
}
