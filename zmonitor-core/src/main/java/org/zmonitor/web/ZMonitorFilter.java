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
import org.zmonitor.MarkerFactory;
import org.zmonitor.MonitorMeta;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorFilter implements Filter {
	
	private boolean isIgnitBySelf;
	
	private HttpRequestMonitorLifecycleManager hReqMSLfManager;
	
	public static final String TRACKER_NAME = "web";
	
	public void init(FilterConfig fConfig) throws ServletException {
		// init ProfilingManager...
		if(ZMonitorManager.isInitialized())return;
		
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		
		//TODO: get Configuration Source...
		ConfigSource configSource = null;
		try {
			configSource = ConfigSources.loadForJavaEEWebApp(fConfig.getServletContext());
		} catch (IOException e) {
			ZMLog.warn(e, "Got some problem while reading: ",
					ConfigSource.WEB_INF_ZMONITOR_XML, ", please make sure it is exist!");
		}
		if(configSource==null){
			ZMLog.warn("cannot find Configuration:[",
					ConfigSource.ZMONITOR_XML,
					"] from current application context: ",ZMonitorFilter.class);
			ZMLog.warn("There's no configuration file loaded, the ZMonitorManager will be configured manually by developer himself.");
			ZMLog.warn("If you want to do configuration by config file, " ,
					"please give your own \"",ConfigSource.ZMONITOR_XML,"\" under /WEB-INF/");
		}
		
		
		aZMonitorManager.performConfiguration(configSource);
		
		hReqMSLfManager = new HttpRequestMonitorLifecycleManager();
		aZMonitorManager.setLifecycleManager(hReqMSLfManager);
		try {
			ZMonitorManager.init(aZMonitorManager);
			isIgnitBySelf = true;
		} catch (AlreadyStartedException e) {
			ZMLog.info("already initialized by other place");
		}
		ZMLog.info(">> Ignit ZMonitor in: ",ZMonitorFilter.class.getCanonicalName());
	}
	public void destroy() {
		if(isIgnitBySelf){
			ZMonitorManager.dispose();
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain filterChain) 
	throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		if(isIgnitBySelf){
			HttpRequestContexts.init(new StantardHttpRequestContext(), 
					req, (HttpServletResponse)res);
			hReqMSLfManager.initRequest( req);	
		}
		
		try{
			ZMonitor.push(newPushTrackingCtx(req));
			
			filterChain.doFilter(req, res);
		}finally{
			
			try{
				ZMonitor.pop(newPopTrackingCtx());	
			}finally{
				if (isIgnitBySelf) {
					hReqMSLfManager.finishRequestIfAny((HttpServletRequest) req);
					HttpRequestContexts.dispose();	
				}
			}
		}
	}
	
	private static TrackingContext newPushTrackingCtx(HttpServletRequest req){
		String mesg = req.getRequestURL()+"?"+req.getQueryString();
		WebConfigurator webConf = ZMonitorManager.getInstance().getBeanIfAny(
				WebConfigurator.class);
		final MonitorMeta mm = webConf.newMonitorMeta("request-start",req);
		
		TrackingContextBase webCtx = new TrackingContextBase(TRACKER_NAME, null){
			public MonitorMeta newMonitorMeta() {
				return mm;
			}};
		webCtx.setMessage(mesg);
		return webCtx;
	}
	
	private static TrackingContext newPopTrackingCtx(){
		TrackingContextBase webCtx = new TrackingContextBase(TRACKER_NAME, null){
			public MonitorMeta newMonitorMeta() {
				return new MonitorMetaBase(
						MarkerFactory.getMarker("request-end"), TRACKER_NAME);
			}
		};
		webCtx.setMessage("<- END");
		return webCtx;
	}

}
