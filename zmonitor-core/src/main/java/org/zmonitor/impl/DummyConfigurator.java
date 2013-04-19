/**SimpleConfigurator.java
 * 2011/4/2
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.ToStringTimelineHandler;
import org.zmonitor.spi.Configurator;

/**
 * this is just for test only, do not really use it. 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class DummyConfigurator implements Configurator{

	public DummyConfigurator(){}

	public void configure(ZMonitorManager manager) {
		/*
		 * Initialize Timeline Handler
		 */
		manager.addMonitorSequenceHandler("DUMMY_TOSTRING_HANDLER", new ToStringTimelineHandler());
	}
	
	
}
