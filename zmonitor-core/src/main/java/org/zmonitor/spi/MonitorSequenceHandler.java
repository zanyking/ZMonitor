/**
 * 2011/3/5
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBean;


/**
 * General Implementation rules:<br>
 * <ol>
 * <li> The name of a MonitorSequenceHandler should point out the destination e.g: Database, Http, Socket, RMI, File.</li>
 * <li> The internal architecture design of MonitorSequenceHandler must let user be able to define the data format that they want to used.</li>
 * <li> To provide user a way to do the configuration, please make the sub class implements {@link org.zmonitor.CustomConfigurable CustomConfiguration}</li>
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
public interface MonitorSequenceHandler extends ZMBean{

	/**
	 * in any case, handle should not throw an exception, because no one outside knows how to handle it. 
	 * @param mSequence
	 */
	public void handle(MonitorSequence mSequence);
	
}
