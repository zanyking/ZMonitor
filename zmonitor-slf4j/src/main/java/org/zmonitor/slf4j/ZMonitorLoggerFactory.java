/**
 * 
 */
package org.zmonitor.slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author Ian YT Tsai(Zanyking)
 * @since 2013/4/23
 */
public class ZMonitorLoggerFactory implements ILoggerFactory {

	private ConcurrentMap<String, Logger> loggerMap;
	
	
	public ZMonitorLoggerFactory(){
		  loggerMap = new ConcurrentHashMap<String, Logger>();
	}
	
	/* (non-Javadoc)
	 * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
	 */
	public Logger getLogger(String name) {
		Logger zmLogger = loggerMap.get(name);
		if(zmLogger!=null){
			return zmLogger;
		}else{
			Logger newInstance = new ZMonitorLogger(name);
			Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
			return oldInstance == null ? newInstance : oldInstance;
		}
		// TODO give a proper impl of ILoggerFactory, please take a look at slf4j-impl. 
	}

}
