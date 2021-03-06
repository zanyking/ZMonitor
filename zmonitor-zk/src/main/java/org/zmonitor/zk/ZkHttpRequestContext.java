/**ZkHttpRequestContext.java
 * 2011/4/1
 * 
 */
package org.zmonitor.zk;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Executions;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.web.HttpRequestContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZkHttpRequestContext implements HttpRequestContext{

	private final HttpRequestContext inner;
	
	/**
	 * 
	 * @param httpReqCtx
	 */
	public ZkHttpRequestContext(HttpRequestContext httpReqCtx){
		inner = httpReqCtx;
	}
	public boolean isInitialized() {
		return inner.isInitialized();
	}
	public void init(HttpServletRequest req, HttpServletResponse res) {
		if(inner.isInitialized()) return;
		inner.init(req, res);
	}
	public void dispose() {
		inner.dispose();
	}
	public HttpServletRequest getRequest() {
		HttpServletRequest req = inner.getRequest();
		if(req==null)
			req = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
		return req;
	}
	public HttpServletResponse getResponse() {
		HttpServletResponse res = inner.getResponse();
		if(res==null)
			res = (HttpServletResponse) Executions.getCurrent().getNativeResponse();
		return res;
	}
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	public String getOriginalRequestURL() {
		return inner.getOriginalRequestURL();
	}

}
