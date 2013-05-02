/**ZKProfilingConfigurator.java
 * 2011/4/5
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zmonitor.CustomConfigurable;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.ConfigContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZPLogConfiguration implements CustomConfigurable {
	
	private boolean enable = true;
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public void configure(ConfigContext webConf) {
		if(enable){
			ZMLog.setLogCore(new LoggerLogDevice(Logger.getLogger(ZMLog.class)));
		}		
	}
	
	
	
	
	
	
}
