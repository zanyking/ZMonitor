/**ZKProfilingConfigurator.java
 * 2011/4/5
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.util.DOMRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZPLogConfiguration implements CustomConfiguration {
	public void apply(ZMonitorManager manager, DOMRetriever xmlDoc, Node configNode) {
		if(enable){
			ZMLog.setLogCore(new LoggerLogDevice(Logger.getLogger(ZMLog.class)));
		}
	}
	
	private boolean enable = true;
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	
	
	
	
	
}
