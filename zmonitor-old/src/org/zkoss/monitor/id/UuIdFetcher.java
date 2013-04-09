/**UuIdFetcher.java
 * 2011/10/11
 * 
 */
package org.zkoss.monitor.id;

import java.util.UUID;

import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.spi.IdFetcher;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class UuIdFetcher implements IdFetcher {

	/* (non-Javadoc)
	 * @see org.zkoss.monitor.IdFetcher#fetch(org.zkoss.monitor.ZMonitorManager)
	 */
	public String fetch(ZMonitorManager zmMgmt) {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

}
