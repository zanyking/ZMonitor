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
import org.zmonitor.MonitorMeta;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.SimpleMonitorMeta;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.marker.Marker;
import org.zmonitor.marker.MarkerFactory;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorServletFilter implements Filter {
	
	private boolean isIgnitBySelf;
	
	private HttpRequestMonitorLifecycleManager hReqMSLfManager;
	
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
					"] from current application context: ",ZMonitorServletFilter.class);
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
		ZMLog.info(">> Ignit ZMonitor in: ",ZMonitorServletFilter.class.getCanonicalName());
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
			
			ZMonitor.push(newTrackingContext("-> "+getQueryURI(req), 
					new WebMonitorMeta(
						MarkerFactory.getMarker("REQUEST_START"), req)));
			
			filterChain.doFilter(req, res);
		}finally{
			
			try{
				ZMonitor.pop(newTrackingContext("<- END", 
						new SimpleMonitorMeta(
							MarkerFactory.getMarker("REQUEST_END"))));	
			}finally{
				if(isIgnitBySelf){// force end...
					hReqMSLfManager.finishRequest((HttpServletRequest) req);
					HttpRequestContexts.dispose();	
				}
			}
		}
	}
	
	private static TrackingContext newTrackingContext(String mesg, MonitorMeta mm){
		TrackingContextBase webCtx = new TrackingContextBase("web");
		webCtx.setMonitorMeta(mm);
		webCtx.setMessage(mesg);
		return webCtx;
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class WebMonitorMeta extends SimpleMonitorMeta{
		protected static final long serialVersionUID = 7285893125167926262L;
		protected String mimeType;
		protected String protocol;
		protected String remoteAddr;
		protected String queryStr;
		protected String remoteUser;
		protected String requestURI;
		protected String servletPath;
		protected String scheme;
		protected String reqSessionId;
		protected String serverName;
		protected int serverPort;
		
		
		protected WebMonitorMeta(){}
		public WebMonitorMeta(Marker marker, HttpServletRequest req) {
			super(marker);
			mimeType = req.getContentType();
			protocol = req.getProtocol();
			remoteAddr = req.getRemoteAddr();
			queryStr = req.getQueryString();
			remoteUser = req.getRemoteUser();
			requestURI = req.getRequestURI();
			scheme = req.getScheme();
			reqSessionId = req.getRequestedSessionId();
			serverName = req.getServerName();
			serverPort = req.getServerPort();
			servletPath = req.getServletPath();
		}
		
		public String getMimeType() {
			return mimeType;
		}

		public String getProtocol() {
			return protocol;
		}

		public String getRemoteAddr() {
			return remoteAddr;
		}

		public String getQueryStr() {
			return queryStr;
		}

		public String getRemoteUser() {
			return remoteUser;
		}

		public String getRequestURI() {
			return requestURI;
		}

		public String getScheme() {
			return scheme;
		}

		public String getReqSessionId() {
			return reqSessionId;
		}

		public String getServerName() {
			return serverName;
		}

		public int getServerPort() {
			return serverPort;
		}

		public String getServletPath() {
			return servletPath;
		}
		
		public boolean isSimilar(MonitorMeta mMeta) {
			if (this == mMeta)
				return true;
			if (!super.isSimilar(mMeta))
				return false;
			
			WebMonitorMeta other = (WebMonitorMeta) mMeta;
			if (queryStr == null) {
				if (other.queryStr != null)
					return false;
			} else if (!queryStr.equals(other.queryStr))
				return false;
			
			if (requestURI == null) {
				if (other.requestURI != null)
					return false;
			} else if (!requestURI.equals(other.requestURI))
				return false;
			if (scheme == null) {
				if (other.scheme != null)
					return false;
			} else if (!scheme.equals(other.scheme))
				return false;
			
			return true;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			
			result = prime * result
					+ ((queryStr == null) ? 0 : queryStr.hashCode());
			result = prime * result
					+ ((requestURI == null) ? 0 : requestURI.hashCode());
			result = prime * result
					+ ((scheme == null) ? 0 : scheme.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			WebMonitorMeta other = (WebMonitorMeta) obj;
			if (mimeType == null) {
				if (other.mimeType != null)
					return false;
			} else if (!mimeType.equals(other.mimeType))
				return false;
			if (protocol == null) {
				if (other.protocol != null)
					return false;
			} else if (!protocol.equals(other.protocol))
				return false;
			if (queryStr == null) {
				if (other.queryStr != null)
					return false;
			} else if (!queryStr.equals(other.queryStr))
				return false;
			if (remoteAddr == null) {
				if (other.remoteAddr != null)
					return false;
			} else if (!remoteAddr.equals(other.remoteAddr))
				return false;
			if (remoteUser == null) {
				if (other.remoteUser != null)
					return false;
			} else if (!remoteUser.equals(other.remoteUser))
				return false;
			if (reqSessionId == null) {
				if (other.reqSessionId != null)
					return false;
			} else if (!reqSessionId.equals(other.reqSessionId))
				return false;
			if (requestURI == null) {
				if (other.requestURI != null)
					return false;
			} else if (!requestURI.equals(other.requestURI))
				return false;
			if (scheme == null) {
				if (other.scheme != null)
					return false;
			} else if (!scheme.equals(other.scheme))
				return false;
			if (serverName == null) {
				if (other.serverName != null)
					return false;
			} else if (!serverName.equals(other.serverName))
				return false;
			if (serverPort != other.serverPort)
				return false;
			return true;
		}

		
		protected WebMonitorMeta clone(){
			WebMonitorMeta clone = new WebMonitorMeta();
			clone.className = this.className;
			clone.fileName = this.fileName;
			clone.lineNumber = this.lineNumber;
			clone.marker = this.marker;
			clone.methodName = this.methodName;
			clone.trackerName = this.trackerName;
			clone.mimeType = this.mimeType;
			clone.protocol = this.protocol;
			clone.remoteAddr = this.remoteAddr;
			clone.queryStr = this.queryStr;
			clone.remoteUser = this.remoteUser;
			clone.requestURI = this.requestURI;
			clone.scheme = this.scheme;
			clone.reqSessionId = this.reqSessionId;
			clone.serverName = this.serverName;
			clone.serverPort = this.serverPort;
			clone.servletPath = this.servletPath;
			return clone;
		}
		
	}//end of class...
	
	private static String getQueryURI(HttpServletRequest req) {
	    String reqUri = req.getRequestURI().toString();
	    String queryString = req.getQueryString();   // d=789
	    if (queryString != null) {
	        reqUri += "?"+queryString;
	    }
	    return reqUri;
	}

}
