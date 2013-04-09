/**
 * Feb 21, 2011
 */
package org.zkoss.monitor.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.monitor.Ignitor;
import org.zkoss.monitor.ZMonitor;
import org.zkoss.monitor.impl.DummyConfigurator;
import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.impl.XmlConfiguratorLoader;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.Configurator;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorServletFilter implements Filter {
	
	private boolean isIgnitBySelf;
	
	public void init(FilterConfig fConfig) throws ServletException {
		// init ProfilingManager...
		if(!Ignitor.isIgnited()){
			Configurator conf = null;
			try {
				conf = XmlConfiguratorLoader.loadForJavaEEWebApp(fConfig.getServletContext());
			} catch (IOException e) {
				ZMLog.warn(e, "Got some problem while reading: ",
						XmlConfiguratorLoader.WEB_INF_ZMONITOR_XML, ", please make sure it is exist!");
			}
			if(conf==null){
				ZMLog.warn("cannot find Configuration:[",
						XmlConfiguratorLoader.ZMONITOR_XML,
						"] from current application context: ",ZMonitorServletFilter.class);
				ZMLog.warn("System will get default configuration from: ",DummyConfigurator.class);
				ZMLog.warn("If you want to give your custom settings, " ,
						"please give your own \"",XmlConfiguratorLoader.ZMONITOR_XML,"\" under /WEB-INF/");
				conf = new DummyConfigurator();
			}
			isIgnitBySelf = Ignitor.ignite(HttpRequestContexts.getTimelineLifecycleManager(), conf);
			ZMLog.info(">> Ignit ZMonitor in: ",ZMonitorServletFilter.class.getCanonicalName());
		}
		
	}
	
	public void destroy() {
		if(isIgnitBySelf){
			Ignitor.destroy();
		}
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) 
	throws IOException, ServletException {
		
		HttpRequestContexts.init(new StantardHttpRequestContext(), 
				(HttpServletRequest)req, (HttpServletResponse)res);
		try{
			ZMonitor.push(new StringName("REQUEST", 
					((HttpServletRequest)req).getRequestURI()), 
					getQueryURI((HttpServletRequest) req));
			
			filterChain.doFilter(req, res);
		}
		finally{
			try{
				ZMonitor.pop("end of request", false);	
			}finally{
				HttpRequestContexts.dispose();	
			}
		}
	}
	
	private static String getQueryURI(HttpServletRequest req) {
	    String reqUri = req.getRequestURI().toString();
	    String queryString = req.getQueryString();   // d=789
	    if (queryString != null) {
	        reqUri += "?"+queryString;
	    }
	    return reqUri;
	}

	
}
