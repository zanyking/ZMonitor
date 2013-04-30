/**
 * Feb 21, 2011
 */
package org.zmonitor.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zmonitor.AlreadyStartedException;
import org.zmonitor.ConfigSource;
import org.zmonitor.Ignitor;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.NullConfigurator;
import org.zmonitor.impl.StringName;
import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.impl.XmlConfiguratorLoader;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorServletFilter implements Filter {
	
	private boolean isIgnitBySelf;
	
	private HttpRequestMonitorSequenceLifecycleManager hReqMSLfManager;
	
	public void init(FilterConfig fConfig) throws ServletException {
		// init ProfilingManager...
		if(!ZMonitorManager.isInitialized()){
			//TODO: get Configuration Source...
			ConfigSource configSource = null;
			
			ZMonitorManager aZMonitorManager = new ZMonitorManager();
			Configurator configurator = null;
			try {
				configurator = XmlConfiguratorLoader.loadForJavaEEWebApp(fConfig.getServletContext());
			} catch (IOException e) {
				ZMLog.warn(e, "Got some problem while reading: ",
						XmlConfiguratorLoader.WEB_INF_ZMONITOR_XML, ", please make sure it is exist!");
			}
			if(configurator==null){
				ZMLog.warn("cannot find Configuration:[",
						XmlConfiguratorLoader.ZMONITOR_XML,
						"] from current application context: ",ZMonitorServletFilter.class);
				ZMLog.warn("System will get default configuration from: ",NullConfigurator.class);
				ZMLog.warn("If you want to give your custom settings, " ,
						"please give your own \"",XmlConfiguratorLoader.ZMONITOR_XML,"\" under /WEB-INF/");
				configurator = new NullConfigurator();
			}
			
			
			aZMonitorManager.setConfigSource(configSource);
			
			
			hReqMSLfManager = new HttpRequestMonitorSequenceLifecycleManager();
			aZMonitorManager.setLifecycleManager(hReqMSLfManager);
			try {
				ZMonitorManager.init(aZMonitorManager);
				isIgnitBySelf = true;
			} catch (AlreadyStartedException e) {
				ZMLog.info("already initialized by other place");
			}
			ZMLog.info(">> Ignit ZMonitor in: ",ZMonitorServletFilter.class.getCanonicalName());
		}
		
	}
	
	public void destroy() {
		if(isIgnitBySelf){
			ZMonitorManager.dispose();
		}
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) 
	throws IOException, ServletException {
		
		if(isIgnitBySelf){
			HttpRequestContexts.init(new StantardHttpRequestContext(), 
					(HttpServletRequest)req, (HttpServletResponse)res);
			hReqMSLfManager.initLifeCycle((HttpServletRequest) req);	
		}
		
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
				if(isIgnitBySelf){
					hReqMSLfManager.disposeLifeCycle((HttpServletRequest) req);
					HttpRequestContexts.dispose();	
				}
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


	/**
	 * 
	 *manage the construction and destruction of MonitorSequenceLifecycle in 
	 * Java Servlet environment.
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	static class HttpRequestMonitorSequenceLifecycleManager implements MonitorSequenceLifecycleManager{

		private static final String KEY_REQ_MSL = "KEY_REQ_MSL";
		private final ThreadLocalMonitorSequenceLifecycleManager thlTManager = 
				new ThreadLocalMonitorSequenceLifecycleManager();
			
		
		public MonitorSequenceLifecycle getLifecycle() {
			HttpRequestContext ctx = HttpRequestContexts.get();
			
			MonitorSequenceLifecycle lfcycle = null;
			if(ctx==null){
				//this is not a Servlet thread, but a thread created by some part of a Java Web application.
				lfcycle = thlTManager.getLifecycle();
			}else{
				lfcycle = (HttpRequestTimelineLifcycle) ctx.getRequest().getAttribute(KEY_REQ_MSL);	
			}
			
			return lfcycle;
		}
		
		public void initLifeCycle(HttpServletRequest req){
			req.setAttribute(KEY_REQ_MSL, 
				new HttpRequestTimelineLifcycle(
						req.getRequestURL().toString()));
		}
		
		public void disposeLifeCycle(HttpServletRequest req){
			req.removeAttribute(KEY_REQ_MSL);
		}
		
	}//end of class...
}
