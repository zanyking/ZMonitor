/**
 * 
 */
package org.zkoss.monitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zkoss.monitor.spi.LogDevice;
import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LoggerLogDevice implements LogDevice{
	
	private final Logger logger;
	/**
	 * 
	 * @param logger
	 */
	public LoggerLogDevice(Logger logger) {
		super();
		this.logger = logger;
	}
	public void warn(Throwable e, Object... strs) {
		logger.warn(Strings.append(strs), e);
	}
	public void debug(Object... strs) {
		logger.debug(Strings.append(strs));
	}
	public boolean shallRecursionPrevented(String loggerName) {
		return logger.getName().equals(loggerName);
	}
	public void warn(Object[] strings) {
		logger.warn(Strings.append(strings));
	}
	public void info(Object... args) {
		logger.info(Strings.append(args));
	}
}
