/**
 * 
 */
package org.zmonitor.selector.impl;


/**
 * Exception thrown for illegal selector string.
 * @author simonpai
 */
public class ParseException extends RuntimeException {
	
	private static final long serialVersionUID = -3279019270343851540L;
	
	public ParseException(int step, Object state, Character input) {
		super("Illegal selector syntax: unexpected character '" + input + 
				"' at index " + (step-1));
	}
	
	public ParseException(String message){
		super(message);
	}
	
}
