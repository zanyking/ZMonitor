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
			StackTraceElement[] elements, String logLevel, String threadId) {
		super(marker, trackerName, elements, threadId);
		this.logLevel = logLevel;
	}


	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

}
