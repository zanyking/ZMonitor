/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LoggerMonitorMeta extends MonitorMetaBase {
	private static final long serialVersionUID = -3699586119435983470L;
	
	
	private String logLevel;

	public LoggerMonitorMeta(Marker marker, String trackerName,
			StackTraceElement stEle, String logLevel) {
		super(marker, trackerName, stEle);
		this.logLevel = logLevel;
	}

	public LoggerMonitorMeta(Marker marker, String trackerName,
			String className, String methodName, int lineNumber, String fileName, String logLevel) {
		super(marker, trackerName, className, methodName, lineNumber, fileName);
		this.logLevel = logLevel;
	}

	public LoggerMonitorMeta(Marker marker, String trackerName, String logLevel) {
		super(marker, trackerName);
		this.logLevel = logLevel;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

}
