/**
 * 
 */
package org.zmonitor.message;

import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class StringMessage extends Message{
	private static final long serialVersionUID = -1107435773751175670L;

	private String string;
	/**
	 * 
	 */
	public StringMessage(){}
	/**
	 * 
	 * @param str
	 */
	public StringMessage(String str){
		this.string = str;
	}
	/**
	 * 
	 * @param objs
	 */
	public StringMessage(Object... objs){
		string = Strings.append(objs);
	}
	/**
	 * 
	 * @return
	 */
	public String getString() {
		return string;
	}
	/**
	 * 
	 * @param string
	 */
	public void setString(String string) {
		this.string = string;
	}

	
	public String toString() {
		return "StringMessage [string=" + string + "]";
	}
	
	
}
