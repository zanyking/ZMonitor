/**SimpleConfigurator.java
 * 2011/4/2
 * 
 */
package org.zkoss.monitor.impl;

import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.handler.ToStringTimelineHandler;
import org.zkoss.monitor.spi.Configurator;

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
		manager.addTimelineHandler("DUMMY_TOSTRING_HANDLER", new ToStringTimelineHandler());
	}
	
	
}
