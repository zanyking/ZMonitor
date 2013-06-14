/**
 * 
 */
package org.zmonitor.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
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

	public Object resolveAttribute(String varName, MonitorPoint mp) {
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
		
		Set<String> classes = new LinkedHashSet<String>();
		useCallerAsCssClasses(classes, meta);
		return classes;
	}
	
	private static void useCallerAsCssClasses(Set<String> classes, MonitorMeta meta){
		if(meta.isCallerNotAvailable())return;
		classes.add(Strings.toShortClassName(meta.getClassName()).replaceAll("[\\W]", "_"));
		classes.add(meta.getMethodName().replaceAll("[\\W]", "_"));
		
	}
	
}
