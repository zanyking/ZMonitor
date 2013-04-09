/**ErrorNotifable.java
 * 2011/10/16
 * 
 */
package org.zmonitor.message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MessageFailover {
	/**
	 * 
	 * @param e
	 */
	public void onError(Exception e);
}
