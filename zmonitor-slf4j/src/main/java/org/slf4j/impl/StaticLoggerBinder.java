/**
 * 
 */
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import org.zmonitor.slf4j.ZMonitorLoggerFactory;

/**
 * @author Ian YT Tsai (Zanyking)
 *
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

	/**
	 * unique instance of StaticLoggerBinder
	 */
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
	
	/**
	 * 
	 * @return the StaticLoggerBinder singleton
	 */
	public static final StaticLoggerBinder getSingleton(){
		return SINGLETON;
	}
	
	/**
	   * Declare the version of the SLF4J API this implementation is compiled
	   * against. The value of this field is usually modified with each release.
	   */
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.6"; // !final

	
	private static final String loggerFacClassName = ZMonitorLoggerFactory.class.getName(); 
	
	private final ILoggerFactory loggerFactory;
	private StaticLoggerBinder(){
		loggerFactory = new ZMonitorLoggerFactory();
	}
	
	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	public String getLoggerFactoryClassStr() {
		return loggerFacClassName;
	}

}
