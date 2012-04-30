/**Base64FormatException.java
 * 2011/10/7
 * 
 */
package org.zkoss.monitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Base64FormatException extends Exception{
	private static final long serialVersionUID = 118356254837938711L;

	/**
	 * Create that kind of exception
	 * @param msg The associated error message 
	 */
	public Base64FormatException(String msg)
	{
		super(msg);
	}

}
