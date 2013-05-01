/**
 * 
 */
package org.zmonitor.spi;

/**
 * 
 * this error only occur when ZMonitor manager try to 
 * load other ZMonitor jars, this error won't be eaten 
 * and the cause should be treated as a ZMonitor bug.<br>   
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfigurationError extends Error {

	/**
	 * 
	 */
	public ConfigurationError() {
	}

	/**
	 * @param message
	 */
	public ConfigurationError(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConfigurationError(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}

}
