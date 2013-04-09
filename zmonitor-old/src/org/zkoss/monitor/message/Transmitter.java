/**Transmitter.java
 * 2011/10/14
 * 
 */
package org.zkoss.monitor.message;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Transmitter {

	/**
	 * 
	 * @param message
	 * @throws TransmisssionException if anything wrong, this exception will thrown directly.
	 */
	public void send(Message message);
	
	/**
	 * 
	 * @param message
	 * @param callback
	 */
	public void post(Message message, Callback callback);
	
}
