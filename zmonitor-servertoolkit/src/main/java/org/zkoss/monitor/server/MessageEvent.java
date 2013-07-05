/**MessageEvent.java
 * 2012/1/12
 * 
 */
package org.zkoss.monitor.server;

import org.zmonitor.message.Message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MessageEvent<T extends Message>{
	private final T message;
	private final boolean hasResponse;
	private Message response;
	/**
	 * 
	 * @param message
	 * @param hasResponse
	 */
	public MessageEvent(T message, boolean hasResponse){
		this.message = message;
		this.hasResponse = hasResponse;
	}
	
	public Message getResponse() {
		return response;
	}
	public void setResponse(Message response) {
		if(!hasResponse){
			throw new UnsupportedOperationException(
				"this event is come from a Message that requires no response!");
		}
		this.response = response;
	}
	
	public T getMessage() {
		return message;
	}
	
	public boolean isHasResponse() {
		return hasResponse;
	}
}
