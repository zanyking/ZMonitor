/**
 * 
 */
package org.zmonitor.selector;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class UndefinedAttributeException extends RuntimeException {
	private static final long serialVersionUID = 6831536517544359143L;

	/**
	 * 
	 */
	public UndefinedAttributeException() {
	}

	/**
	 * @param message
	 */
	public UndefinedAttributeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UndefinedAttributeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UndefinedAttributeException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * 
	 * @param clz
	 * @param name
	 */
	public UndefinedAttributeException(Class clz, String name) {
		super("unable to find attribute: "+name+" from type: "+clz);
	}

}
