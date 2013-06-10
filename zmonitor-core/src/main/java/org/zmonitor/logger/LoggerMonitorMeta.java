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
	
	

	public LoggerMonitorMeta() {}

	public LoggerMonitorMeta(Marker marker, String trackerName,
			StackTraceElement stEle) {
		super(marker, trackerName, stEle);
	}

	public LoggerMonitorMeta(Marker marker, String trackerName,
			String className, String methodName, int lineNumber, String fileName) {
		super(marker, trackerName, className, methodName, lineNumber, fileName);
	}

	public LoggerMonitorMeta(Marker marker, String trackerName) {
		super(marker, trackerName);
	}

}
