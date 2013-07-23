/**
 * 
 */
package org.zmonitor.web.filter;

import java.net.URL;

/**
 * the implementation must be thread-safe.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface UrlFilter {
	/**
	 * 
	 * @param urlStr
	 * @return
	 */
	public boolean shouldAccept(URL url);
}
