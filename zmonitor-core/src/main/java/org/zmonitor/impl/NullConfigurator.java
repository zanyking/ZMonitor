/**SimpleConfigurator.java
 * 2011/4/2
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.Configurator;

/**
 * this is just for test only, do not really use it. 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class NullConfigurator implements Configurator{

	public NullConfigurator(){}


	public void configure(ConfigContext configCtx) {
		//DO NOTHING...
		/*
		 * Initialize Timeline Handler
		 */
//		manager.addMonitorSequenceHandler("DUMMY_TOSTRING_HANDLER", new ToStringTimelineHandler());
	}
	
	
}
