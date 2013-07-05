/**IgnitionFailureException.java
 * 2011/4/4
 * 
 */
package org.zkoss.monitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class IgnitionFailureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1384691009563832959L;

	/**
	 * 
	 */
	public IgnitionFailureException() {
	}

	/**
	 * @param message
	 */
	public IgnitionFailureException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IgnitionFailureException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IgnitionFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
