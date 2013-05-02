/**
 * 
 */
package org.zmonitor.spi;

/**
 * 
 * this error only occur when ZMonitor manager failed to load other ZMonitor jars,
 * this error won't be eaten and the cause should be treated as a bug.<br>   
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LookUpError extends Error {
	private static final long serialVersionUID = 8675840341070993700L;

	/**
	 * 
	 */
	public LookUpError() {
	}

	/**
	 * @param message
	 */
	public LookUpError(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LookUpError(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LookUpError(String message, Throwable cause) {
		super(message, cause);
	}

}
