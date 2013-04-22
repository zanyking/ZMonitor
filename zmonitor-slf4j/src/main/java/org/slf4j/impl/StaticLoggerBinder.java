/**
 * 
 */
package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @author Ian YT Tsai (Zanyking)
 *
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

	/**
	   * Declare the version of the SLF4J API this implementation is compiled
	   * against. The value of this field is usually modified with each release.
	   */
	// to avoid constant folding by the compiler, this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.6"; // !final

	  
	public ILoggerFactory getLoggerFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLoggerFactoryClassStr() {
		// TODO Auto-generated method stub
		return null;
	}

}
