/**
 * 
 */
package org.zmonitor.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorListener implements ServletContextListener {

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("ZMonitorServletContextListener::contextInitialized: "+sce.getSource());
		ServletContext servletCtx = sce.getServletContext();
		ZMonitorFilterHelper helper = 
			ZMonitorFilterHelper.getInstance(servletCtx);
		helper.init(servletCtx);
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("ZMonitorServletContextListener::contextDestroyed: "+sce.getSource());
		ServletContext servletCtx = sce.getServletContext();
		ZMonitorFilterHelper helper = 
			ZMonitorFilterHelper.getInstance(servletCtx);
		helper.destroy();
	}
	
	
}
