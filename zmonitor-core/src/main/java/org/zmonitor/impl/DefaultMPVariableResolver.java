/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.MonitorPoint;
import org.zmonitor.util.GetterInvocationCache;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultMPVariableResolver {

	protected MonitorPoint mp;
	
	/**
	 * 
	 * @param mp
	 */
	public DefaultMPVariableResolver(MonitorPoint mp) {
		this.mp = mp;
	}
	/**
	 * 
	 * @param varName
	 * @return
	 */
	public Object resolveVariable(String varName) {
		return GetterInvocationCache.SINGELTON.invoke(varName, mp.getMonitorMeta());
	}

	
}


