/**Agent.java
 * 2011/10/14
 * 
 */
package org.zkoss.monitor.message;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Communicator {
	/**
	 * 
	 * @return a transmitter that you can send message from Master.
	 */
	public Transmitter getTransmitter();
	/**
	 * 
	 * @return a Receiever that will receive message from Master.
	 */
	public Receiever getReceiever();
}
