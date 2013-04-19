/**TimelineHandler.java
 * 2011/3/5
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.MonitorSequence;


/**
 * General Implementation rules:<br>
 * <ol>
 * <li> The name of a MonitorSequenceHandler should point out the destination e.g: Database, Http, Socket, RMI, File.</li>
 * <li> The internal architecture design of MonitorSequenceHandler must let user be able to define the data format that they want to used.</li>
 * <li> To provide user a way to do the configuration, please make the sub class implements {@link org.zmonitor.spi.CustomConfiguration CustomConfiguration}</li>
 * <li> Only a few orthogonal features that can shared between handlers, for example:
 * 		<ul>
 *		<li> Asynchronous processing Module: Incoming MonitorSequences will be queued and consumed by different thread.        
 *		<li> MonitorSequence Marshaling Module: could be simple Java ObjectOutput,  configurable Marshaling engine which 
 *will accept templates for MonitorSequence & Monitor Point or entirely customized binary format.
 *		</ul>
 *</li>  
 * </ol>
 * @author Ian YT Tsai(Zanyking)
 */
public interface MonitorSequenceHandler {

	/**
	 * 
	 * @param mSequence
	 */
	public void handle(MonitorSequence mSequence);
	//TODO: component life cycle method should be centralized. 
	/**
	 * This method will be called when MonitorSequenceHandler need to be destroyed.
	 */
	public void destroy();
	
}
