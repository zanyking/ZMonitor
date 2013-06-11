package org.zmonitor;
/**
 * if a ZMonitor instance is already started, this exception will be thrown.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AlreadyStartedException extends Exception {

	public AlreadyStartedException() {
	}

	public AlreadyStartedException(String message) {
		super(message);
	}

	public AlreadyStartedException(Throwable cause) {
		super(cause);
	}

	public AlreadyStartedException(String message, Throwable cause) {
		super(message, cause);
	}

}
