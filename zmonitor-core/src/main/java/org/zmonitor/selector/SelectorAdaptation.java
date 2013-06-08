/**
 * 
 */
package org.zmonitor.selector;

import java.util.Set;

import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface SelectorAdaptation {

	/**
	 * 
	 * @param mp
	 * @return
	 */
	public String retrieveId(MonitorPoint mp);
	/**
	 * 
	 * @param mp
	 * @return
	 */
	public String retrieveType(MonitorPoint mp) ;
	/**
	 * 
	 * @param mp
	 * @return
	 */
	public Set<String> retrieveConceptualCssClasses(MonitorPoint mp);
	/**
	 * 
	 * @param varName
	 * @param mp
	 * @return
	 */
	public Object resolveVariable(String varName, MonitorPoint mp);
}
