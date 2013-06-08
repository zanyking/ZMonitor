/**
 * 
 */
package org.zmonitor.selector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.marker.Marker;
import org.zmonitor.marker.Markers;
import org.zmonitor.util.Strings;

/**
 * Common mp attribute can be used in Selector:
 * <ol>
 *  <li>
 *  <li>
 *  <li>
 *  <li>
 * </ol>
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorAdaptor implements SelectorAdaptation{

	
	private final Map<String, SelectorAdaptation> supports = 
			new HashMap<String, SelectorAdaptation>();
	
	
	private final DefaultSelectorAdaptation inner = 
			new DefaultSelectorAdaptation();
	
	/**
	 * 
	 * @param trackerName
	 * @param support
	 */
	public void addSupport(String trackerName, SelectorAdaptation support){
		supports.put(trackerName, support);
	}
	
	protected SelectorAdaptation get(MonitorPoint mp){
		String trackerName = mp.getMonitorMeta().getTrackerName();
		
		if(Markers.TRACKER_NAME_ZM.equals(trackerName)){
			return inner;
		}
		
		SelectorAdaptation sup = supports.get(trackerName);
		if(sup ==null) sup = inner;
		return sup;
	}
	
	public String retrieveId(MonitorPoint mp) {
		return get(mp).retrieveId(mp);
	}
	

	public String retrieveType(MonitorPoint mp) {
		return get(mp).retrieveType(mp);
	}

	public Set<String> retrieveConceptualCssClasses(MonitorPoint mp) {
		return get(mp).retrieveConceptualCssClasses(mp);
	}
	

	public Object resolveVariable(String varName, MonitorPoint mp) {
		return get(mp).resolveVariable(varName, mp);
	}

	

	
	
}