/**
 * 
 */
package org.zmonitor;

/**
 * In ZMonitor module implementation, throw this exception if some part must not
 * be called after ZMonitorManager initialization.
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class HasNotInitializedException extends Exception {

	/**
	 * 
	 */
	public HasNotInitializedException() {
	}

	/**
	 * @param message
	 */
	public HasNotInitializedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public HasNotInitializedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HasNotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

}
