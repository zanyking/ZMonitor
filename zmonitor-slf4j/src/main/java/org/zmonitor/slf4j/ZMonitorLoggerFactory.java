/**
 * 
 */
package org.zmonitor.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author Ian YT Tsai(Zanyking)
 * @since 2013/4/23
 */
public class ZMonitorLoggerFactory implements ILoggerFactory {

	
	/* (non-Javadoc)
	 * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
	 */
	public Logger getLogger(String name) {
		// TODO give a proper impl of ILoggerFactory, please take a look at slf4j-impl. 
		return null;
	}

}
