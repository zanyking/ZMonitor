/**
 * 
 */
package org.zmonitor.webtest;

import org.apache.http.StatusLine;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class UnableToReceiveResultException extends RuntimeException {
	private static final long serialVersionUID = -7670062145189937637L;
	private StatusLine statusLine;
	/**
	 * 
	 */
	public UnableToReceiveResultException() {
	}

	/**
	 * @param message
	 */
	public UnableToReceiveResultException(StatusLine statusLine) {
		super(statusLine.toString());
		this.statusLine = statusLine;
	}
	/**
	 * 
	 * @return
	 */
	public StatusLine getStatusLine() {
		return statusLine;
	}

	/**
	 * @param cause
	 */
	public UnableToReceiveResultException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnableToReceiveResultException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UnableToReceiveResultException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
