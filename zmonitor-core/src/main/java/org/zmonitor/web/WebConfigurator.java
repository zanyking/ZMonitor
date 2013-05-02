/**
 * 
 */
package org.zmonitor.web;

import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.Configs;
import org.zmonitor.spi.Configurator;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebConfigurator implements Configurator {
	
	public static final String REL_WEB_CONF = "web-conf";
	
	public void configure(ConfigContext monitorMgmt) {
		ConfigContext webConf = monitorMgmt.toNode(REL_WEB_CONF);
		
		if(webConf.getNode()==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		Configs.initCustomConfigurable(webConf, 
				new JavaWebConfCustom(), true);
		
	}
	
	
	
		
}
