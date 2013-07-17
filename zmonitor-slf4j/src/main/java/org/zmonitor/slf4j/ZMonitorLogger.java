/**
 * 
 */
package org.zmonitor.slf4j;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.zmonitor.HasNotInitializedException;
import org.zmonitor.MonitorMeta;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.LoggerMonitorMeta;
import org.zmonitor.logger.MessageTuple;
import org.zmonitor.logger.TrackerBase;
import org.zmonitor.util.StackTraceElementFinder;

/**
 * 
 * Zmonitor implementation of Logger that sends all enabled log messages, for
 * all defined loggers, to Zmonitor.
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class ZMonitorLogger implements Logger, Serializable{
	private static final long serialVersionUID = -5063731853235642189L;
	
//	private static class Not
	/**
	 * a smart reference to Slf4jConfigurator
	 */
	private static class ConfigRef{
		private Slf4jConfigurator slf4jConfig = null;

		
		public Slf4jConfigurator get() throws HasNotInitializedException {
			if (slf4jConfig != null)
				return slf4jConfig;
			slf4jConfig = ZMonitorManager.getInstance().getBeanIfAny(
					Slf4jConfigurator.class);
			if (slf4jConfig == null) {
				throw new HasNotInitializedException(
						"the ZMonitorManager hasn't initialized yet, please initialize it before use slf4j ZMonitor Adaptor.");
			}
			return slf4jConfig;
		}
	}//end of class...
	private ConfigRef configRef = new ConfigRef();

	
	private static final StackTraceElementFinder ST_ELEMENT_FINDER = 
			new StackTraceElementFinder(
					"java.lang",
					"org.zmonitor",
					"org.slf4j");

	/** The current log level */
	
	private LogLevel currentLogLevel;
	
	protected LogLevel getCurrentLogLevel(){
		if(currentLogLevel==null){
			try {
				currentLogLevel = configRef.get().getLogLevel(name);
			} catch (HasNotInitializedException e) {
				ZMLog.warn(e, "getCurrentLogLevel() ",
					"has been called while ZMonitorManager hasn't been started yet.");
				return LogLevel.INFO;
			}	
		}
		return currentLogLevel;
	}

	private String name;

	ZMonitorLogger(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	private static Object[] trimmedCopy(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			throw new IllegalStateException(
					"non-sensical empty or null argument array");
		}
		final int trimemdLen = argArray.length - 1;
		Object[] trimmed = new Object[trimemdLen];
		System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
		return trimmed;
	}
	
	private static MessageTuple newMessageTuple(String messagePattern,
			Object... argArray) {
		Throwable throwable = getThrowableCandidate(argArray);
		if (throwable != null) {
			argArray = trimmedCopy(argArray);
		}
		return new MessageTuple(messagePattern, argArray, throwable);
	}
	private static final Throwable getThrowableCandidate(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			return null;
		}

		final Object lastEntry = argArray[argArray.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}
	/**
	 * Responsibility:
	 * <ol>
	 * <li> tracker name
	 * <li> create {@link org.zmonitor.Slf4jTrackingContext}
	 * <li> gathering callerInfo( config: should)
	 * <li>
	 * <li>
	 * <li>
	 * </ol> 
	 * @param level
	 * @param marker
	 * @param mt
	 */
	private void log0(LogLevel level, Marker marker, MessageTuple mt) {
		
		// TODO: zmonitor slf4j logging...
		// more than dozens of properties can be used at this place.
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		
		elements = ST_ELEMENT_FINDER.truncate(elements);
		final LoggerMonitorMeta mm = new LoggerMonitorMeta(
				adapt(marker), 
				Markers.TRACKER_NAME_SLF4J, 
				elements, 
				level.toString());
		
		TrackingContextBase tCtx = new TrackingContextBase(Markers.TRACKER_NAME_SLF4J, elements){
			public MonitorMeta newMonitorMeta() {
				return mm;
			}
			
		};
		tCtx.setMessage(mt);
		getTracker().doTrack(tCtx);
	}
	
	private TrackerBase slf4jTracker; 
	private TrackerBase getTracker(){
		if(slf4jTracker==null){
			try {
				slf4jTracker = configRef.get().getTracker();
			} catch (HasNotInitializedException e) {
				ZMLog.warn(e, "getCurrentLogLevel() ",
						"has been called while ZMonitorManager hasn't been started yet.");
				//do nothing because ZMonitor is not ready to serve...
				return TrackerBase.NULL_TRACKER;//do nothing...
			}	
		}
		return slf4jTracker;
	}
	
	protected org.zmonitor.marker.Marker adapt(Marker mk){
		return (org.zmonitor.marker.Marker) mk;
	}
	
	/**
	 * 
	 * @param level
	 * @param marker
	 * @param message
	 * @param t
	 */
	private void log(LogLevel level, Marker marker, String message, Throwable t) {
		if (!isLevelEnabled(level)) {
			return;
		}
		MessageTuple mt = new MessageTuple(message, null, t);
		log0(level, marker, mt);
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 * 
	 * @param level
	 * @param format
	 * @param arg1
	 * @param arg2
	 */
	private void formatAndLog(LogLevel level, Marker marker, String format,
			Object arg1, Object arg2) {
		if (!isLevelEnabled(level)) {
			return;
		}
		MessageTuple mt = newMessageTuple(format, new Object[]{arg1, arg2});
		log0(level, marker,mt);
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 * 
	 * @param level
	 * @param format
	 * @param arguments
	 *            a list of 3 ore more arguments
	 */
	private void formatAndLog(LogLevel level, Marker marker, String format,
			Object... arguments) {
		if (!isLevelEnabled(level)) {
			return;
		}
		MessageTuple mt = newMessageTuple(format, arguments);
		log0(level, marker,mt);
	}
	 
	/**
	 * Is the given log level currently enabled?
	 * 
	 * @param logLevel
	 *            is this level enabled?
	 */
	protected boolean isLevelEnabled(LogLevel logLevel) {
		return (logLevel.greaterOrEquals(getCurrentLogLevel()));
	}

	public boolean isTraceEnabled() {
		return isLevelEnabled(LogLevel.TRACE);
	}
	public boolean isTraceEnabled(Marker marker) {
		return isTraceEnabled();//ignore marker by now.
	}
	public void trace(Marker marker, String msg) {
		log(LogLevel.TRACE, marker, msg, null);
	}
	public void trace(String msg) {
		log(LogLevel.TRACE, null, msg, null);
	}
	public void trace(Marker marker, String format, Object arg) {
		formatAndLog(LogLevel.TRACE, marker, format, arg, null);
	}
	public void trace(String format, Object arg) {
		formatAndLog(LogLevel.TRACE, null, format, arg, null);
	}
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.TRACE, marker, format, arg1, arg2);		
	}
	public void trace(String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.TRACE, null, format, arg1, arg2);
	}
	public void trace(Marker marker, String format, Object... argArray) {
		formatAndLog(LogLevel.TRACE, marker, format, argArray);
	}
	public void trace(String format, Object... arguments) {
		formatAndLog(LogLevel.TRACE, null, format, arguments);
	}
	public void trace(Marker marker, String msg, Throwable t) {
		log(LogLevel.TRACE, marker, msg, t);		
	}
	public void trace(String msg, Throwable t) {
		log(LogLevel.TRACE, null, msg, t);
	}
	
	
	public boolean isDebugEnabled() {
		return isLevelEnabled(LogLevel.DEBUG);
	}
	public boolean isDebugEnabled(Marker marker) {
		return isDebugEnabled();
	}
	public void debug(String msg) {
		log(LogLevel.DEBUG, null, msg, null);
	}
	public void debug(Marker marker, String msg) {
		log(LogLevel.DEBUG, marker, msg, null);
	}
	public void debug(String format, Object arg) {
		formatAndLog(LogLevel.DEBUG, null, format, arg, null);
	}
	public void debug(Marker marker, String format, Object arg) {
		formatAndLog(LogLevel.DEBUG, marker, format, arg, null);		
	}
	public void debug(String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.DEBUG, null, format, arg1, arg2);
	}
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.DEBUG, marker, format, arg1, arg2);		
	}
	public void debug(String format, Object... arguments) {
		formatAndLog(LogLevel.DEBUG, null, format, arguments);
	}
	public void debug(Marker marker, String format, Object... arguments) {
		formatAndLog(LogLevel.DEBUG, marker, format, arguments);
	}
	public void debug(String msg, Throwable t) {
		log(LogLevel.DEBUG, null, msg, t);
	}
	public void debug(Marker marker, String msg, Throwable t) {
		log(LogLevel.DEBUG, marker, msg, t);		
	}
	
	
	public boolean isInfoEnabled() {
		return isLevelEnabled(LogLevel.INFO);
	}
	public boolean isInfoEnabled(Marker marker) {
		return isInfoEnabled();// ignore marker by now.
	}
	public void info(String msg) {
		log(LogLevel.INFO, null, msg, null);
	}
	public void info(Marker marker, String msg) {
		log(LogLevel.INFO, marker, msg, null);
	}
	public void info(String msg, Throwable t) {
		log(LogLevel.INFO, null, msg, t);
	}
	public void info(Marker marker, String msg, Throwable t) {
		log(LogLevel.INFO, marker, msg, t);
	}
	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.INFO, null, format, arg1, arg2);
	}
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.INFO, marker, format, arg1, arg2);
	}
	public void info(String format, Object arg) {
		formatAndLog(LogLevel.INFO, null, format, arg, null);
	}
	public void info(Marker marker, String format, Object arg) {
		formatAndLog(LogLevel.INFO, marker, format, arg, null);
	}
	public void info(String format, Object... arguments) {
		formatAndLog(LogLevel.INFO, null, format, arguments);
	}
	public void info(Marker marker, String format, Object... arguments) {
		formatAndLog(LogLevel.INFO, marker, format, arguments);
	}
	
	
	public boolean isWarnEnabled() {
		return isLevelEnabled(LogLevel.WARN);
	}
	public boolean isWarnEnabled(Marker marker) {
		return isWarnEnabled();// ignore marker by now.
	}
	public void warn(String msg) {
		log(LogLevel.WARN, null, msg, null);
	}
	public void warn(Marker marker, String msg) {
		log(LogLevel.WARN, marker, msg, null);
	}
	public void warn(String format, Object arg) {
		formatAndLog(LogLevel.WARN, null, format, arg, null);
	}
	public void warn(Marker marker, String format, Object arg) {
		formatAndLog(LogLevel.WARN, marker, format, arg, null);
	}
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.WARN, null, format, arg1, arg2);
	}
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.WARN, marker, format, arg1, arg2);
	}
	public void warn(String format, Object... arguments) {
		formatAndLog(LogLevel.WARN, null, format, arguments);
	}
	public void warn(Marker marker, String format, Object... arguments) {
		formatAndLog(LogLevel.WARN, marker, format, arguments);
	}
	public void warn(String msg, Throwable t) {
		log(LogLevel.WARN, null, msg, t);
	}
	public void warn(Marker marker, String msg, Throwable t) {
		log(LogLevel.WARN, marker, msg, t);
	}
	
	
	public boolean isErrorEnabled() {
		return isLevelEnabled(LogLevel.ERROR);
	}
	public boolean isErrorEnabled(Marker marker) {
		return isErrorEnabled();//ignore marker by now.
	}
	public void error(String msg) {
		log(LogLevel.ERROR, null, msg, null);
	}
	public void error(Marker marker, String msg) {
		log(LogLevel.ERROR, marker, msg, null);
	}
	public void error(String format, Object arg) {
		formatAndLog(LogLevel.ERROR, null, format, arg, null);
	}
	public void error(Marker marker, String format, Object arg) {
		formatAndLog(LogLevel.ERROR, marker, format, arg, null);
	}
	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.ERROR, null, format, arg1, arg2);
	}
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		formatAndLog(LogLevel.ERROR, marker, format, arg1, arg2);
	}
	public void error(String format, Object... arguments) {
		formatAndLog(LogLevel.ERROR, null, format, arguments);
	}
	public void error(Marker marker, String format, Object... arguments) {
		formatAndLog(LogLevel.ERROR, marker, format, arguments);
	}
	public void error(String msg, Throwable t) {
		log(LogLevel.ERROR, null, msg, t);
	}
	public void error(Marker marker, String msg, Throwable t) {
		log(LogLevel.ERROR, marker, msg, t);
	}

}
