/**
 * 
 */
package org.zmonitor.webtest;

import org.apache.http.StatusLine;
import org.zmonitor.test.junit.MonitoredResult;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebTestResponse {

	private MonitoredResult fMonitoredResult;
	
	public MonitoredResult getMonitoredResult(){
		return fMonitoredResult;
	}
	
	public void setMonitoredResult(MonitoredResult mRes){
		fMonitoredResult = mRes;
	}
	
	private StatusLine statusLine;
	public StatusLine getStatusLine() {
		return statusLine;
	}
	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}

	
}
