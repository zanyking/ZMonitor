/**
 * 
 */
package org.zmonitor.slf4j.marker;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.zmonitor.slf4j.marker.AdaptiveMarker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AdaptiveMarkerFactory implements IMarkerFactory {


	  Map markerMap = new HashMap();
	  
	  /**
	   * Regular users should <em>not</em> create
	   * <code>AdaptiveMarkerFactory</code> instances. <code>Marker</code>
	   * instances can be obtained using the static {@link
	   * org.slf4j.MarkerFactory#getMarker} method.
	   */
	  public AdaptiveMarkerFactory() {
	  }

	  /**
	   * Manufacture a {@link AdaptiveMarker} instance by name. If the instance has been 
	   * created earlier, return the previously created instance. 
	   * 
	   * @param name the name of the marker to be created
	   * @return a Marker instance
	   */
	  public synchronized Marker getMarker(String name) {
	    if (name == null) {
	      throw new IllegalArgumentException("Marker name cannot be null");
	    }

	    Marker marker = (Marker) markerMap.get(name);
	    if (marker == null) {
	      marker = new AdaptiveMarker(name);
	      markerMap.put(name, marker);
	    }
	    return marker;
	  }
	  
	  /**
	   * Does the name marked already exist?
	   */
	  public synchronized boolean exists(String name) {
	    if (name == null) {
	      return false;
	    }
	    return markerMap.containsKey(name);
	  }

	  public boolean detachMarker(String name) {
	    if(name == null) {
	      return false;
	    }
	    return (markerMap.remove(name) != null);
	  }

	  
	  public Marker getDetachedMarker(String name) {
	    return  new AdaptiveMarker(name);
	  }
	  

}
