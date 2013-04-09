/**
 * 
 */
package org.zmonitor.web;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.zmonitor.Ignitor;
import org.zmonitor.impl.DummyConfigurator;
import org.zmonitor.impl.XmlConfiguratorLoader;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.Configurator;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorInitContextListener implements ServletContextListener {
	
	
	private boolean isIgnitBySelf;
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sCtxEvent) {
		if(!Ignitor.isIgnited()){
			Configurator conf = null;
			
			try {
				conf = XmlConfiguratorLoader.loadForJavaEEWebApp(sCtxEvent.getServletContext());
			} catch (IOException e) {
				ZMLog.warn(e, "Got some problem while reading: "+
						XmlConfiguratorLoader.WEB_INF_ZMONITOR_XML+ ", please make sure it is exist!");
			}
			if(conf==null){
				ZMLog.warn("cannot find Configuration:["+
						XmlConfiguratorLoader.WEB_INF_ZMONITOR_XML+
						"] from current application context: "+ZMonitorInitContextListener.class);
				ZMLog.warn("System will get default configuration from: "+DummyConfigurator.class);
				ZMLog.warn("If you want to give your custom settings, " +
						"please give your own \""+XmlConfiguratorLoader.ZMONITOR_XML+"\" under /WEB-INF/");
				conf = new DummyConfigurator();
			}
			isIgnitBySelf = Ignitor.ignite(HttpRequestContexts.getTimelineLifecycleManager(), conf);
			ZMLog.info(">> Ignit ZMonitor in: "+ZMonitorInitContextListener.class.getCanonicalName());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sCtxEvent) {
		if(isIgnitBySelf){
			Ignitor.destroy();
		}
	}
}
