/**
 * 
 */
package org.zkoss.monitor.server;

import org.zmonitor.message.Message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ResponseContext {

	
	/**
	 * 
	 * @param response
	 */
	public void setResponse(Message response);
	
	/**
	 * 
	 * @return
	 */
	public Message getResponse();
}
