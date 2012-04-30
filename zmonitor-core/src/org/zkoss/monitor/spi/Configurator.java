/**Configurator.java
 * 2011/4/4
 * 
 */
package org.zkoss.monitor.spi;

import org.zkoss.monitor.ZMonitorManager;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Configurator {
	/**
	 * 
	 * @param manager
	 */
	public void configure(ZMonitorManager manager);
}
