/**IgnitionFailureException.java
 * 2011/4/4
 * 
 */
package org.zmonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class InitFailureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1384691009563832959L;

	/**
	 * 
	 */
	public InitFailureException() {
	}

	/**
	 * @param message
	 */
	public InitFailureException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InitFailureException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
