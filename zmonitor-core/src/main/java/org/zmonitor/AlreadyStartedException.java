package org.zmonitor;
/**
 * if a ZMonitor instance is already started, this exception will be thrown.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AlreadyStartedException extends Exception {

	private static final long serialVersionUID = -5745757214627581401L;

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
