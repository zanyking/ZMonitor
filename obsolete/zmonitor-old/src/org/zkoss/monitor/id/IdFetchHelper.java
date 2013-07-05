/**IdFetchHelper.java
 * 2011/10/11
 * 
 */
package org.zkoss.monitor.id;

import org.zkoss.monitor.Agent;
import org.zkoss.monitor.ZMonitorManager;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class IdFetchHelper {
	
	private final ZMonitorManager zMgmt;
	/**
	 * 
	 * @param zMgmt
	 */
	public IdFetchHelper(ZMonitorManager zMgmt){
		this.zMgmt = zMgmt;
	}

	public String getPersistedId(){
		String oldId = null;
		Agent master = zMgmt.getAgent();
		return oldId;
	}
}
