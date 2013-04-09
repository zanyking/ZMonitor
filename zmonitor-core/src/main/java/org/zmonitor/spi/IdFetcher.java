/**IdFetcher.java
 * 2011/10/11
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.ZMonitorManager;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface IdFetcher {

	/**
	 * fetch Id according to current ZMonitor and environment.
	 * @param zmMgmt
	 * @return an id represent this ZMonitor instance.
	 */
	public String fetch(ZMonitorManager zmMgmt);
}
