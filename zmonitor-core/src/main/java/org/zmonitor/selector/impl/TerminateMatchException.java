/**
 * 
 */
package org.zmonitor.selector.impl;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TerminateMatchException extends RuntimeException {

	private static final long serialVersionUID = -4507348439838754113L;

	/**
	 * 
	 */
	public TerminateMatchException() {
		super("no matches for the rest nodes.");
	}

	/**
	 * @param message
	 */
	public TerminateMatchException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TerminateMatchException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TerminateMatchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public TerminateMatchException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
