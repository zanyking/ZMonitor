/**Requests.java
 * 2011/4/1
 * 
 */
package org.zmonitor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		HttpRequestContext context = get();
		if(context==null)return;
		context.dispose();
		HTTP_REQ_CTX_REF.remove();
	}
	
	
}
