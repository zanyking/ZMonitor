/**Receiever.java
 * 2011/10/14
 * 
 */
package org.zmonitor.message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Receiever {

	/**
	 * 
	 * @param listener
	 */
	public void setHandler(MessageHandler listener);
	/**
	 * 
	 * @param req
	 * @return
	 */
	public Message handle(Message req);
}
