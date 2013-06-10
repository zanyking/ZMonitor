/**
 * 
 */
package org.zmonitor.slf4j;

import org.slf4j.spi.LocationAwareLogger;

/**
 * An enum to wrap {@link LocationAwareLogger}'s log code.
 * @author Ian YT Tsai(Zanyking)
 *
 */
public enum LogLevel {
	/**
	 * the finest log level for slf4j, usually used in DEV ENV to trace a bug.
	 */
	TRACE(LocationAwareLogger.TRACE_INT),
	/**
	 * a default log level for DEV ENV to deal with occasional issues.
	 */
	DEBUG(LocationAwareLogger.DEBUG_INT), 
	/**
	 * a log level to provide a context with general information to higher level
	 * logs.
	 */
	INFO(LocationAwareLogger.INFO_INT), 
	/**
	 * a log level to indicate there's something should be noticed   
	 */
	WARN(LocationAwareLogger.WARN_INT), 
	/**
	 * the most serious log level for slf4j.
	 */
	ERROR(LocationAwareLogger.ERROR_INT);
	private int code;
	private LogLevel(int code){
		this.code = code;
	}
	public int toCode(){
		return code;
	}
	public boolean greaterOrEquals(LogLevel lv){
		return code >= lv.code;
	}
	public static LogLevel toLogLevel(int code){
		if(TRACE.toCode()==code)return TRACE;
		if(DEBUG.toCode()==code)return DEBUG;
		if(INFO.toCode()==code)return INFO;
		if(WARN.toCode()==code)return WARN;
		if(ERROR.toCode()==code)return ERROR;
		throw new IllegalArgumentException("the given code is not apart of LocationAwareLogger's consts, code:"+code);
	}
	
}
