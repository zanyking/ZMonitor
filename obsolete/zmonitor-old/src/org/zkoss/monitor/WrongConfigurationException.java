/**WrongConfigurationException.java
 * 2011/4/4
 * 
 */
package org.zkoss.monitor;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class WrongConfigurationException extends RuntimeException {
	private static final long serialVersionUID = -2123873221753160461L;

	public WrongConfigurationException(){}
	/**
	 * @param message
	 */
	public WrongConfigurationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WrongConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WrongConfigurationException(String message,
			Throwable cause) {
		super(message, cause);
	}

}
