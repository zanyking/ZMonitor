/**
 * 
 */
package org.zmonitor.web.filter;

/**
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
