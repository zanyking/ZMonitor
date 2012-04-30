/**TimelineHandler.java
 * 2011/3/5
 * 
 */
package org.zkoss.monitor.spi;

import org.zkoss.monitor.Timeline;


/**
 * General Implementation rules:<br>
 * <ol>
 * <li> The name of a TimelineHandler should point out the destination channel it will used, ex: Database, Http, Socket, RMI, File storage.</li>
 * <li> The internal architecture design of TimelineHandler must let user be able to define the data format that they want to used.</li>
 * <li> To provide user a way to do the configuration, please make the sub class implements {@link org.zkoss.monitor.spi.CustomConfiguration CustomConfiguration}</li>
 * <li> Different destinations should has it's own internal architecture according to it's nature.</li>
 * <li> Only a few orthogonal features that can shared between TimelineHandlers, for example:
 * 		<ul>
 *		<li> Asynchronous processing Module: Incoming Timelines will be queued and consumed by different thread.        
 *		<li> Timeline Marshaling Module: could be simple Java ObjectOutput,  configurable Marshaling engine which 
 *will accept templates for Timeline & Measuring Point or entirely customized binary format.
 *		</ul>
 *</li>  
 * </ol>
 * @author Ian YT Tsai(Zanyking)
 */
public interface TimelineHandler {

	/**
	 * 
	 * @param execTLine
	 */
	public void handle(Timeline execTLine);
	/**
	 * This method will be called while Timeline Handler need to be destroyed.
	 */
	public void destroy();
	
}
