/**
 * 
 */
package org.zmonitor.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.marker.Marker;
import org.zmonitor.marker.Markers;
import org.zmonitor.selector.SelectorAdaptation;
import org.zmonitor.util.Strings;

/**
 * 
 * <b>id:</b><br>
 * <i>the escape result of : short(meta.getClassName())+"_"+meta.getMethodName()+"_"+meta.getLineNumber();</i><br>
 * Please take a look at the code of: {@code #retrieveId(MonitorPoint)};  
 * 
 * <b>type:</b><br>
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultSelectorAdaptation implements SelectorAdaptation{

	/**
	 * 
	 */
	public DefaultSelectorAdaptation() {
		
	}

	public String retrieveId(MonitorPoint mp) {
		return retrieveId(mp.getMonitorMeta());
	}

	public String retrieveType(MonitorPoint mp) {
		return Markers.retrieveRegisteredMajorMarker(mp.getMonitorMeta()).getName();
	}

	public Set<String> retrieveConceptualCssClasses(MonitorPoint mp) {
		return retrieveConceptualCssClasses(mp.getMonitorMeta());
	}

	public Object resolveVariable(String varName, MonitorPoint mp) {
		return new DefaultMPVariableResolver(mp).resolveVariable(varName);
	}
	/**
	 * 
	 * @param meta
	 * @return
	 */
	public static String retrieveId(MonitorMeta meta) {
		if(meta.isCallerNotAvailable())
			return null;
		
		meta.getMethodName();
		
		return Strings.escapeId(Strings.append(
			Strings.toShortClassName(meta.getClassName()),
			"_",meta.getMethodName()
			,"_",meta.getLineNumber()));
	}
	
	/**
	 * 
	 * @param meta
	 * @return
	 */
	public static Set<String> retrieveConceptualCssClasses(MonitorMeta meta) {
		
		Set<String> classes = new HashSet<String>();
		useTrackerClassAsCssClasses(classes, meta);
		return classes;
	}
	
	private static void useMarkerAsCssClasses(Set<String> classes, MonitorMeta meta){
		// marker as css class...
		Marker mk =  Markers.retrieveRegisteredMajorMarker(meta);
		classes.add(mk.getName());
		Iterator<Marker> it = mk.iterator();
		Marker reference;
		while (it.hasNext()) {
			reference =  it.next();
			classes.add(reference.getName());
		}
	}
	
	
	private static void useTrackerClassAsCssClasses(Set<String> classes, MonitorMeta meta){
		if(meta.isCallerNotAvailable())return;
		classes.add(Strings.toShortClassName(meta.getClassName()));
		
	}
}
