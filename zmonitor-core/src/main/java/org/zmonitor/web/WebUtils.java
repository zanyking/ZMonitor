/**
 * 
 */
package org.zmonitor.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebUtils {
	private WebUtils() {}
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public static String toURLString(HttpServletRequest req){
		StringBuffer reqUrl = req.getRequestURL();
		
		String query = req.getQueryString();
		
		if(query!=null && !query.isEmpty()){
			reqUrl.append("?").append(query);
		}
			
		return reqUrl.toString();
	}
	
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public static URL toURL(HttpServletRequest req){
		String urlStr = toURLString(req);
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			//Wont' happen, if happened...there might be some bad things happened in container. 
			throw new Error(e);
		}
		return url;
	}
	
	
	

}
