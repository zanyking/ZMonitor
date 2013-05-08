/**
 * 
 */
package org.zmonitor.slf4j;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 
 * Zmonitor implementation of Logger that sends all enabled log messages, for
 * all defined loggers, to Zmonitor.
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class ZMonitorLogger extends MarkerIgnoringBase {
	private static final long serialVersionUID = -5063731853235642189L;

	private static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
	private static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
	private static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
	private static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
	private static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;

	
	/** The current log level */
	protected int currentLogLevel = LOG_LEVEL_INFO;

	private String name;
	ZMonitorLogger(String name) {
		this.name = name;
	}

	private void log(int level, String message, Throwable t) {
		    if (!isLevelEnabled(level)) {
		      return;
		    }
		    //TODO: zmonitor slf4j logging...
		    // more than dozens of properties can be used at this place.
		    // 
		    
	 }
	 
	 /**
	   * For formatted messages, first substitute arguments and then log.
	   *
	   * @param level
	   * @param format
	   * @param arg1
	   * @param arg2
	   */
	  private void formatAndLog(int level, String format, Object arg1,
	                            Object arg2) {
	    if (!isLevelEnabled(level)) {
	      return;
	    }
	    FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
	    log(level, tp.getMessage(), tp.getThrowable());
	  //TODO might have to handle the message pattern directly in the future(for performance issue)
	  }

	  /**
	   * For formatted messages, first substitute arguments and then log.
	   *
	   * @param level
	   * @param format
	   * @param arguments a list of 3 ore more arguments
	   */
	  private void formatAndLog(int level, String format, Object... arguments) {
	    if (!isLevelEnabled(level)) {
	      return;
	    }
	    FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
	    log(level, tp.getMessage(), tp.getThrowable());
	    //TODO might have to handle the message pattern directly in the future(for performance issue)
	  }
	 
	/**
	 * Is the given log level currently enabled?
	 * 
	 * @param logLevel
	 *            is this level enabled?
	 */
	protected boolean isLevelEnabled(int logLevel) {
		// log level are numerically ordered so can use simple numeric
		// comparison
		return (logLevel >= currentLogLevel);
	}

	public boolean isTraceEnabled() {
		return isLevelEnabled(LOG_LEVEL_TRACE);
	}

	public void trace(String msg) {
		log(LOG_LEVEL_TRACE, msg, null);
	}

	public void trace(String format, Object arg) {
		formatAndLog(LOG_LEVEL_TRACE, format, arg, null);
	}

	public void trace(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_TRACE, format, arg1, arg2);
	}

	public void trace(String format, Object... arguments) {
		formatAndLog(LOG_LEVEL_TRACE, format, arguments);
	}

	public void trace(String msg, Throwable t) {
		log(LOG_LEVEL_TRACE, msg, t);
	}

	
	
	public boolean isDebugEnabled() {
		return isLevelEnabled(LOG_LEVEL_DEBUG);
	}

	public void debug(String msg) {
		log(LOG_LEVEL_DEBUG, msg, null);
	}

	public void debug(String format, Object arg) {
		formatAndLog(LOG_LEVEL_DEBUG, format, arg, null);
	}

	public void debug(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_DEBUG, format, arg1, arg2);
	}

	public void debug(String format, Object... arguments) {
		formatAndLog(LOG_LEVEL_DEBUG, format, arguments);
	}

	public void debug(String msg, Throwable t) {
		log(LOG_LEVEL_DEBUG, msg, t);
	}

	
	
	
	public boolean isInfoEnabled() {
		return isLevelEnabled(LOG_LEVEL_INFO);
	}

	public void info(String msg) {
		log(LOG_LEVEL_INFO, msg, null);
	}

	public void info(String format, Object arg) {
		formatAndLog(LOG_LEVEL_INFO, format, arg, null);
	}

	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
	}

	public void info(String format, Object... arguments) {
		formatAndLog(LOG_LEVEL_INFO, format, arguments);
	}

	public void info(String msg, Throwable t) {
		log(LOG_LEVEL_INFO, msg, t);
	}

	
	
	
	public boolean isWarnEnabled() {
		return isLevelEnabled(LOG_LEVEL_WARN);
	}

	public void warn(String msg) {
		log(LOG_LEVEL_WARN, msg, null);
	}

	public void warn(String format, Object arg) {
		formatAndLog(LOG_LEVEL_WARN, format, arg, null);
	}

	public void warn(String format, Object... arguments) {
		formatAndLog(LOG_LEVEL_WARN, format, arguments);

	}

	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
	}

	public void warn(String msg, Throwable t) {
		log(LOG_LEVEL_WARN, msg, t);
	}

	
	
	public boolean isErrorEnabled() {
		return isLevelEnabled(LOG_LEVEL_ERROR);
	}

	public void error(String msg) {
		log(LOG_LEVEL_ERROR, msg, null);
	}

	public void error(String format, Object arg) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
	}

	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
	}

	public void error(String format, Object... arguments) {
		formatAndLog(LOG_LEVEL_ERROR, format, arguments);
	}

	public void error(String msg, Throwable t) {
		log(LOG_LEVEL_ERROR, msg, t);

	}

}
