/**ZPLog.java
 * 2011/4/4
 * 
 */
package org.zkoss.monitor.impl;

import org.zkoss.monitor.spi.LogDevice;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMLog {

	private static LogDevice logCore = new ConsoleLogDevice();
	
	public static LogDevice getLogCore() {
		return logCore;
	}
	public static void setLogCore(LogDevice logCore) {
		ZMLog.logCore = logCore;
	}
	/**
	 * 
	 * @param args
	 */
	public static void debug(Object... args){
		logCore.debug(args);
	}
	/**
	 * 
	 * @param args
	 */
	public static void info(Object... args) {
		logCore.info(args);
	}
	/**
	 * 
	 * @param e
	 * @param strings
	 */
	public static void warn( Throwable e, Object... strings) {
		logCore.warn(e, strings);
	}
	/**
	 * 
	 * @param strings
	 */
	public static void warn(Object... strings) {
		logCore.warn(strings);
	}

}
