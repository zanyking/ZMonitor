/**RenderObj.java
 * 2011/4/6
 * 
 */
package org.zkoss.monitor.web.zk;

import org.zkoss.monitor.spi.Name;

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
