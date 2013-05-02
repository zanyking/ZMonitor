/**
 * 
 */
package org.zmonitor.web.filter;

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
	public boolean shouldAccept(String urlStr);
}
