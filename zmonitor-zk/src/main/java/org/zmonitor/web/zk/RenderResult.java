/**RenderObj.java
 * 2011/4/6
 * 
 */
package org.zmonitor.web.zk;

import org.zmonitor.spi.Name;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class RenderResult {
	private final Name name;
	private final String message;
	/**
	 * 
	 * @param name
	 * @param message
	 */
	public RenderResult(Name name, String message) {
		super();
		this.name = name;
		this.message = message;
	}
	public Name getName() {
		return name;
	}
	public String getMessage() {
		return message;
	}
}
