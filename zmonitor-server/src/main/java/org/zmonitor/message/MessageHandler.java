/**ReceiveListener.java
 * 2011/10/14
 * 
 */
package org.zmonitor.message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MessageHandler {
	/**
	 * 
	 * @param request
	 * @param hasCallback
	 * @return response message
	 * @throws Exception
	 */
	public Message handle(Message request, boolean hasCallback)throws Exception;
}
