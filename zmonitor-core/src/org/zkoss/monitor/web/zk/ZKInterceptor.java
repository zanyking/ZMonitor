/**
 * 
 */
package org.zkoss.monitor.web.zk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.monitor.Ignitor;
import org.zkoss.monitor.MeasurePoint;
import org.zkoss.monitor.ZMonitor;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.impl.DummyConfigurator;
import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.impl.XmlConfiguratorLoader;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.Configurator;
import org.zkoss.monitor.web.HttpRequestContext;
import org.zkoss.monitor.web.HttpRequestContexts;
import org.zkoss.monitor.web.StantardHttpRequestContext;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.Monitor;
import org.zkoss.zk.ui.util.PerformanceMeter;
import org.zkoss.zk.ui.util.RequestInterceptor;
import org.zkoss.zk.ui.util.URIInterceptor;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZKInterceptor implements WebAppInit, WebAppCleanup, 
	RequestInterceptor,  
	ExecutionInit, ExecutionCleanup, URIInterceptor, 
	EventInterceptor, Monitor, PerformanceMeter, Initiator{
	
	/* ******************************
	 *  WebApp Init & Cleanup interface
	 * ******************************/

	private static ZMonitorManager getPfMgmt(){
		return ZMonitorManager.getInstance();
	}
	
	private static final ZKInterceptorConfiguration DEFAULT_CONFIG = new ZKInterceptorConfiguration();
	private static ZKInterceptorConfiguration getConfiguration(){
		ZKInterceptorConfiguration conf = getPfMgmt().getCustomConfiguration(ZKInterceptorConfiguration.class);
		if(conf==null)
			conf = DEFAULT_CONFIG;
		return conf;
	}
	private static ZKInterceptorMPRenderer getRenderer(){
		return getConfiguration().getMpRenderer();
	}
	
	private static boolean hasLifecycleControl = false;
	
	public void init(WebApp wapp) throws Exception {
		if(!Ignitor.isIgnited()){
			Configurator conf = null;
			try {
				conf = XmlConfiguratorLoader.loadForJavaEEWebApp((ServletContext) wapp.getNativeContext());
			} catch (IOException e) {
				ZMLog.warn(e, ZKInterceptor.class.getSimpleName()," is not able to ignit ZMonitor by reading config from: "+
						XmlConfiguratorLoader.WEB_INF_ZMONITOR_XML, ", please make sure it is exist!");
			}
			if(conf==null){
				ZMLog.warn("cannot find Configuration:[",
						XmlConfiguratorLoader.ZMONITOR_XML,
						"] from current application context: ",
						ZKInterceptor.class);
				
				ZMLog.warn("System will get default configuration from: ",DummyConfigurator.class);
				ZMLog.warn("If you want to give your custom settings, " ,
						"please give your own \"",XmlConfiguratorLoader.ZMONITOR_XML,"\" under /WEB-INF/");
				conf = new DummyConfigurator();
			}
				
			hasLifecycleControl = 
				Ignitor.ignite(HttpRequestContexts.getTimelineLifecycleManager(), conf);
			ZMLog.info("Ignit ZMonitor in: ",ZKInterceptor.class.getName());

		}
	}
	
	public void cleanup(WebApp wapp) throws Exception {
		if(hasLifecycleControl){
			Ignitor.destroy();
		}
			
	}
	
	
	
	/* ***********************************
	 *  Request & URI Intercepter interface
	 * ***********************************/	
	public void request(Session sess, Object req, Object res) {// ZK begin to handle a request
		if(hasLifecycleControl){
			HttpRequestContext httpReqCtx = HttpRequestContexts.get();
			if(httpReqCtx==null){
				HttpRequestContexts.init(
						new ZkHttpRequestContext(new StantardHttpRequestContext()), 
						(HttpServletRequest)req, (HttpServletResponse)res);
			}else{
				HttpRequestContexts.init(new ZkHttpRequestContext(httpReqCtx), 
						(HttpServletRequest)req, (HttpServletResponse)res);
			}
		}
	}
	private static HttpRequestContext getContext(){
		return HttpRequestContexts.get();
	}
	
	private static final String KEY_REQUEST_ZUL_URI = "KEY_REQUEST_ZUL_URI";
	
	public void request(String uri) throws Exception {
		if(getPfMgmt().getTimelineLifecycle().hasTimelineStarted()){
			getContext().getRequest().setAttribute(KEY_REQUEST_ZUL_URI, uri);
			RenderResult result = getRenderer().getURIInterceptorResult(uri);
			if(getConfiguration().isRenderURIIntercepter())
				ZMonitor.record(result.getName(), result.getMessage());
		}
	}
	public static String getCurrentRequestZulURI(){
		String currURL = (String)getContext().getRequest().getAttribute(KEY_REQUEST_ZUL_URI);
		if(currURL==null){
			currURL = getContext().getRequest().getRequestURI();
		}
		return currURL;
	}
	
	/* ***********************************
	 *  Execution Init & Cleanup interface
	 * ***********************************/	

	public void init(Execution exec, Execution parent) throws Exception {
		String uri = getCurrentRequestZulURI();
		RenderResult result = getRenderer().getExecutionInitResult(exec, parent, uri);
		if(getConfiguration().isRenderExecutionInit())
			ZMonitor.push(result.getName(), result.getMessage());
	}
	
	@SuppressWarnings("rawtypes")
	public void cleanup(Execution exec, Execution parent, List errs) throws Exception {
		RenderResult result = getRenderer().getExecutionCleanupResult(exec, parent);
		if(getConfiguration().isRenderExecutionInit()){
			ZMonitor.pop(result.getName(), result.getMessage());
		}
		if(hasLifecycleControl){//TODO: This part shouldn't be done here, ZK need to support RequestEnd!
//			if(!getPfMgmt().getTimelineLifcycle().isStarted()){// the last stack of 
//				HttpRequestContexts.dispose();	
//			}
		}
	}

	/* ******************************
	 *  Event Intercepter interface
	 * ******************************/
	
	public Event beforeSendEvent(Event event) {
		if(getConfiguration().isRenderBeforeSendEvent()){
			RenderResult result = getRenderer().getBeforeSendEventResult(event);
			ZMonitor.record(result.getName(), result.getMessage());	
		}
		return event;
	}

	public Event beforePostEvent(Event event) {
		if(getConfiguration().isRenderBeforePostEvent()){
			RenderResult result = getRenderer().getBeforePostEventResult(event);
			ZMonitor.record(result.getName(), result.getMessage());	
		}
		return event;
	}

	public Event beforeProcessEvent(Event event) {
		if(getConfiguration().isRenderProcessEvent()){
			RenderResult result = getRenderer().getBeforeProcessEventResult(event);
			ZMonitor.push(result.getName(), result.getMessage());	
		}
		return event;
	}

	public void afterProcessEvent(Event event) {
		if(getConfiguration().isRenderProcessEvent()){
			RenderResult result = getRenderer().getAfterProcessEventResult(event);
			ZMonitor.pop(result.getName(), result.getMessage());	
//			ZMonitor.end(new ZkEventName("AFTER_EVENT_PROC", event));	
		}
	}

	/* ******************************
	 *  Monitor interface
	 * ******************************/
	

	public void sessionCreated(Session sess) {}
	public void sessionDestroyed(Session sess) {}
	public void desktopCreated(Desktop desktop) {}
	public void desktopDestroyed(Desktop desktop) {}

	/**
	 * Because the Component inside AuRequest is not applicable while {@link Monitor#beforeUpdate(Desktop, List)}<br> 
	 * So I need to "keep" these AuRequests in Execution scope, and retrieve them after they are handled by ZK Event Queue.
	 * 
	 * @author Ian YT Tsai  
	 */
	private static class AuRecContext{
		final List<AuRequest> requests;
		final MeasurePoint auStartMpoint;
		final Desktop desktop;
		@SuppressWarnings("unchecked")
		public AuRecContext(@SuppressWarnings("rawtypes") List requests, MeasurePoint auStartRec, Desktop desktop) {
			super();
			this.requests = requests;
			this.auStartMpoint = auStartRec;
			this.desktop = desktop;
			
			if(this.auStartMpoint==null){
				throw new IllegalArgumentException("measure point is null");
			}
		}
		public void applyName(){
			RenderResult result = getRenderer().getMonitorAsyncUpdateResult(requests, desktop);
			auStartMpoint.name = result.getName();
			auStartMpoint.message = result.getMessage();
		}
	}//end of class...
	private static final String KEY_ASYNC_UPDATE_RECORD = "KEY_ASYNC_UPDATE_RECORD";
	@SuppressWarnings("rawtypes")
	public void beforeUpdate(Desktop desktop, List requests) {
		if(getConfiguration().isRenderMonitorAsyncUpdate()){
			MeasurePoint rec = ZMonitor.push(null);
			if(rec!=null){//measure it
				getContext().getRequest().setAttribute(KEY_ASYNC_UPDATE_RECORD, 
					new AuRecContext(requests, rec, desktop));
			}
		}
	}
	
	public void afterUpdate(Desktop desktop) {
		if(getConfiguration().isRenderMonitorAsyncUpdate()){
			AuRecContext context = 
				(AuRecContext) getContext().getRequest().getAttribute(KEY_ASYNC_UPDATE_RECORD);
			if(context!=null) {
				context.applyName();
			}
			ZMonitor.pop("after Async Update", false);
		}
	}
	
	/* ******************************
	 *  Initiator interface
	 * ******************************/
	@SuppressWarnings("rawtypes")
	public void doInit(Page page,  Map args) throws Exception {
		String prefix = (String) args.get("prefix");
		if(prefix==null){
			prefix = ZKInterceptor.getCurrentRequestZulURI();
		}
		ZMonitor.push(new StringName("INIT", prefix), "ProfilingInit::doInit()");	
	}
	public void doFinally() throws Exception {
		ZMonitor.pop();		
	}
	public void doAfterCompose(Page page) throws Exception {}
	public boolean doCatch(Throwable ex) throws Exception {
		return false;// forward this Throwable to other initiators.
	}

	/* ******************************
	 *  Performance Meter interface
	 * ******************************/

	/*
	 *    A - B
	 * E      |
	 *  \ D - C
	 *  
	 * To eliminate the System Delta Time between Client and Server, we can only has Network Time. 
	 * 
	 * Network Time = D_req + D_res = SSs - CSc + CEc - SEs  
	 * Process Time = SEs - SSs
	 * Client Render Time = CRc - CEc
	 * 
	 */
	public void requestStartAtClient(String requestId, Execution exec, long time) {
//		System.out.println("Performance Meter["+requestId+"]: Client -> :"+ time);
	}
	
	public void requestStartAtServer(String requestId, Execution exec, long time) {
//		System.out.println("Performance Meter["+requestId+"]: -> Server :"+ time);
	}

	public void requestCompleteAtServer(String requestId, Execution exec, long time) {
//		System.out.println("Performance Meter["+requestId+"]: <- Server :"+ time);
	}

	public void requestReceiveAtClient(String requestId, Execution exec, long time) {
//		System.out.println("Performance Meter["+requestId+"]: Client <- :"+ time);
	}

	public void requestCompleteAtClient(String requestId, Execution exec, long time) {
//		System.out.println("Performance Meter["+requestId+"]: Client CC :"+ time);
	}




}
