/**PropertySetterException.java
 * 2011/4/5
 * 
 */
package org.zkoss.monitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class PropertySetterException extends RuntimeException {
	private static final long serialVersionUID = -613932551687789252L;

	
	public PropertySetterException() {
		super();
	}

	public PropertySetterException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertySetterException(String message) {
		super(message);
	}

	public PropertySetterException(Throwable cause) {
		super(cause);
	}

}
