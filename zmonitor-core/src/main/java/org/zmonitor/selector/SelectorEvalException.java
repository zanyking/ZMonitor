/**
 * 
 */
package org.zmonitor.selector;

/**
 * <p>
 * Throw this exception when ever you found there's an error occurred in
 * Selector expression evaluation, for example:
 * 
 * <p>
 * While implementing a {@link org.zmonitor.selector.impl.PseudoClassDef<T>},
 * you might throw this exception if there's anything wrong with parameters.
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class SelectorEvalException extends RuntimeException {
	private static final long serialVersionUID = -313564439015273596L;

	/**
	 * 
	 */
	public SelectorEvalException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SelectorEvalException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SelectorEvalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SelectorEvalException(String message, Throwable cause) {
		super(message, cause);
	}

}
