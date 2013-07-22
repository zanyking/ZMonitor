/**
 * 
 */
package org.zmonitor.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author ian.tsai
 *
 */
public class ZMonitorFilterHelper {
	private boolean isIgnitBySelf;
	private HttpRequestMonitorLifecycleManager hReqMSLfManager;

	private static final String TRACKER_NAME = ZMonitorFilter.TRACKER_NAME;
	
	
	/**
	 */
	public static ZMonitorFilterHelper getInstance(ServletContext servletCtx) {
		ZMonitorFilterHelper helper = (ZMonitorFilterHelper) servletCtx
				.getAttribute(ZMonitorFilterHelper.class.getName());
		if (helper == null) {
			synchronized (servletCtx) {
				if (helper == null) {

					servletCtx.setAttribute(
							ZMonitorFilterHelper.class.getName(),
							helper = new ZMonitorFilterHelper());
				}
			}

		}
		return helper;
	}
	/**
	 * 
	 * @param servletCtx
	 */
	public void init(ServletContext servletCtx) {
		// init ProfilingManager...
		if(ZMonitorManager.isInitialized())return;
		
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		
		//TODO: get Configuration Source...
		ConfigSource configSource = null;
		try {
			configSource = ConfigSources.loadForJavaEEWebApp(servletCtx);
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
	/**
	 * 
	 */
	public void destroy() {
		if(isIgnitBySelf){
			ZMonitorManager.dispose();
		}
	}
	
	/**
	 * 
	 * @param req
	 * @param res
	 */
	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		
		boolean isZmUsed = ZMonitorManager.isInitialized();
		
		if(isZmUsed && isIgnitBySelf){
			HttpRequestContexts.init(new StantardHttpRequestContext(), 
					req, res);
			hReqMSLfManager.initRequest( req);	
		}
		
		try{
			if(isZmUsed)
				ZMonitor.push(newPushTrackingCtx(req));
			
			filterChain.doFilter(req, res);
		}finally{
			if(isZmUsed){
				try{
					ZMonitor.pop(newPopTrackingCtx());	
				}finally{
					if (isIgnitBySelf) {
						hReqMSLfManager.finishRequestIfAny( req);
						HttpRequestContexts.dispose();	
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	private TrackingContext newPushTrackingCtx(HttpServletRequest req){
		String query = req.getQueryString();
		
		String mesg = req.getRequestURL()+
				(query==null? "" : ("?"+req.getQueryString()));
		
		WebConfigurator webConf = ZMonitorManager.getInstance().getBeanIfAny(
				WebConfigurator.class);
		MonitorMeta mm = webConf.newMonitorMeta("request-start",req);
		
		WebTrackingContextBase webCtx = new WebTrackingContextBase(null, mm);
		webCtx.setMessage(mesg);
		return webCtx;
	}
	/**
	 * 
	 * @return
	 */
	private TrackingContext newPopTrackingCtx(){
		MonitorMeta mm = new MonitorMetaBase(
				MarkerFactory.getMarker("request-end"), TRACKER_NAME, null);
		WebTrackingContextBase webCtx = new WebTrackingContextBase( null, mm);
		webCtx.setMessage("<- END");
		return webCtx;
	}

	/**
	 * 
	 * @author Ian YT Tsai (Zanyking)
	 *
	 */
	private class WebTrackingContextBase extends TrackingContextBase{
		private MonitorMeta fMonitorMeta;

		public WebTrackingContextBase(StackTraceElement[] stackTraceElements, 
				MonitorMeta fMonitorMeta) {
			super(TRACKER_NAME, stackTraceElements);
			this.fMonitorMeta = fMonitorMeta;
		}

		@Override
		public MonitorMeta newMonitorMeta() {
			return fMonitorMeta;
		}
		
		public MonitorLifecycle getLifeCycle() {
			if(isIgnitBySelf)
				return hReqMSLfManager.getLifecycle();
			return ZMonitorManager.getInstance().getMonitorLifecycle();
		}
	}
	
	
	
	
}
