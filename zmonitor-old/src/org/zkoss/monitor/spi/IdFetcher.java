/**IdFetcher.java
 * 2011/10/11
 * 
 */
package org.zkoss.monitor.spi;

import org.zkoss.monitor.ZMonitorManager;

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
