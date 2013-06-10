/**
 * 
 */
package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;
import org.zmonitor.slf4j.marker.AdaptiveMarkerFactory;

/**
 * The binding of {@link MarkerFactory} class with an actual instance of
 * {@link IMarkerFactory} is performed using information returned by this class.
 * 
 * @author Ian YT Tsai(Zanyking)
 * @since 2013/4/23
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {

	public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

	final IMarkerFactory markerFactory = new AdaptiveMarkerFactory();

	private StaticMarkerBinder() {
	}

	/**
	 * Currently this method always returns an instance of
	 * {@link BasicMarkerFactory}.
	 */
	public IMarkerFactory getMarkerFactory() {
		return markerFactory;
	}

	/**
	 * Currently, this method returns the class name of
	 * {@link BasicMarkerFactory}.
	 */
	public String getMarkerFactoryClassStr() {
		return BasicMarkerFactory.class.getName();
	}

}
