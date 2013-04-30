/**Configurator.java
 * 2011/4/4
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.ZMonitorManager;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Configurator {
	/**
	 * 
	 * @param manager
	 * @param ctxt
	 */
	public void configure(ZMonitorManager manager, XMLConfiguration ctxt);
}
