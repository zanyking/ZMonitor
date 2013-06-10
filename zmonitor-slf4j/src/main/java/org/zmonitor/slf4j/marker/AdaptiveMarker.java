/**
 * 
 */
package org.zmonitor.slf4j.marker;

import org.slf4j.Marker;
import org.zmonitor.marker.BasicMarker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AdaptiveMarker extends BasicMarker implements Marker{

	private static final long serialVersionUID = -8806003870581397616L;

	AdaptiveMarker(String name) {
		super(name);
	}

	public void add(Marker reference) {
		this.add((org.zmonitor.marker.Marker)reference);
	}

	public boolean remove(Marker reference) {
		return this.remove((org.zmonitor.marker.Marker)reference);
	}

	public boolean hasChildren() {
		return this.hasReferences();
	}

	public boolean contains(Marker other) {
		return this.contains((org.zmonitor.marker.Marker)other);
	}

}
