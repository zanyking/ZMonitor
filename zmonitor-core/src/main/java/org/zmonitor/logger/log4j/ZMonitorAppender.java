/**
 * 
 */
package org.zmonitor.logger.log4j;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.zmonitor.AlreadyStartedException;
import org.zmonitor.InitFailureException;
import org.zmonitor.MarkerFactory;
import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.log4j.NdcContext.NdcObj;
import org.zmonitor.marker.Marker;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorAppender extends AppenderSkeleton {

	protected boolean javaSourceLocationInfo = true;
	protected String mpNameType = "LOG4J";



	private static ZMonitorAppender singleton;
	
	public ZMonitorAppender(){
		this.name = "ZMonitor";
		synchronized (ZMonitorAppender.class) {
			if(singleton ==null)
				singleton = this;
		}
	}
	
	private boolean testMode;
	
	public boolean isTestMode(){
		return testMode;
	}
	public void setTestMode(boolean b) {
		testMode = b;
	}
	
	protected boolean embedded;//default is false
	public boolean isEmbedded() {
		return embedded;
	}
	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}
	/**
	 * 
	 * @return
	 */
	public static ZMonitorAppender getInstance(){
		return singleton;
	}
	
	/**
	 * Gets whether the location of the logging request call should be captured.
	 * 
	 * @since 1.2.16
	 * @return the current value of the <b>LocationInfo</b> option.
	 */
	public boolean getLocationInfo() {
		return javaSourceLocationInfo;
	}

	/**
	 * The <b>LocationInfo</b> option takes a boolean value. By default, it is
	 * set to true;
	 * <p/>
	 * <p/>
	 * if you set this to true, please remember to turn this setting  off after performance testing.
	 * </p>
	 * 
	 * @since 1.2.16
	 * @param flag true if location information should be extracted.
	 */
	public void setLocationInfo(final boolean flag) {
		javaSourceLocationInfo = flag;
	}
	/**
	 * 
	 * @return
	 */
	public String getMonitorPointNameType() {
		return mpNameType;
	}
	/**
	 * 
	 * @param mpNameType
	 */
	public void setMonitorPointNameType(String mpNameType) {
		this.mpNameType = mpNameType;
	}


	public boolean requiresLayout() {
		return false;
	}
	
	private boolean isIgnitBySelf;
	@Override
	public void activateOptions() {
		super.activateOptions();
		
		if(ZMonitorManager.isInitialized() || isEmbedded())return;
		try {
			ZMonitorManager aZMonitorManager = new ZMonitorManager();
			//create configuration Source...
			final ConfigSource configSrc = ConfigSources.loadForSimpleJavaProgram();
			if(configSrc!=null){
				aZMonitorManager.performConfiguration(configSrc);
			}
			ZMonitorManager.init(aZMonitorManager);
			aZMonitorManager.setLifecycleManager(new ThreadLocalMonitorLifecycleManager());
			isIgnitBySelf = true;
			ZMLog.info(">> Ignite ZMonitor in: ",ZMonitorAppender.class.getCanonicalName());
		} catch (IOException e) {
			throw new InitFailureException(e);
		} catch (AlreadyStartedException e) {
			ZMLog.info("ZMonitorManager is already initialized");
		}
	}
	public void close() {
		if(isIgnitBySelf){
			ZMonitorManager.dispose();
		}
	}

	/*(non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#doAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	public synchronized void doAppend(LoggingEvent event) {
		super.doAppend(event);
	}
	/*(non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent event) {
		
		String ndcStr = null;
		int depth = -1;
		{//IMPORTANT: this section is something MUST be called!
			ndcStr = event.getNDC();
		    depth = NDC.getDepth();
		    event.getThreadName();
		    // Get a copy of this thread's MDC.
		    event.getMDCCopy();
		}
	    
	    if(preventRecursion(this.getClass(), event.getLoggerName()) || 
	    		preventRecursion(ZMLog.class, event.getLoggerName()))
	    	return;
	    
	    
	    {//IMPORTANT: this section is something MUST be called!
	    	String mesg = event.getRenderedMessage();
	    	event.getThrowableStrRep();
	    	
	    	/*
	    	 * SCENARIO:
	    	 *  while tl!=started, we suppose to start a new tl.
	    	 *  log4j shoulden't terminate other's tl, it can only terminate it's selves.
	    	 * 
	    	 *  depth=0, tl==started, controlBySelf		 [complete] this tl is reaching it's end, should be terminated.
	    	 *  depth=0, tl==started, controlByOthers	[recording] this tl is controlled by others, log4j is simply an interceptor, do recording.
	    	 *  
	    	 *  depth=0, tl!=started, controlBySelf		[doNothing] because tl can only be started while depth>0.
	    	 *  depth=0, tl!=started, controlByOthers	[doNothing] because tl can only be started while depth>0.
	    	 *  
	    	 *  depth>0, tl==started, controlBySelf		[recording] watch out the depth problem. 
	    	 *  depth>0, tl==started, controlByOthers	[recording] watch out the depth problem.
	    	 *  
	    	 *  depth>0, tl!=started, controlBySelf		 [do Start] 
	    	 *  depth>0, tl!=started, controlByOthers	 do nothing... 
	    	 *  
	    	 */
	    	MonitorLifecycle lfc = ZMonitorManager.getInstance().getMonitorLifecycle();
	    	
//	    	System.out.printf("ZMonitorAppender::append() depth=%1$2s ,isControlledBySelf=%2$b, isMonitorStarted=%3$b, ZMonitor.isMonitoring()=%4$b \n" ,
//	    			depth, isControlledBySelf(lfc), lfc.isMonitorStarted(), ZMonitor.isMonitoring());
	    	
	    	if(depth==0){
	    		if(ZMonitor.isMonitoring()){
	    			if(isControlledBySelf(lfc)){
	    				complete(event, mesg, lfc);
	    			}else{
	    				record(event, depth, lfc, ndcStr);
	    			}
				}
	    		// do nothing...
	    		//tl.start must satisfy (depth > 0), otherwise there's no way for appender to know when to complete MonitorSequence.
	    	}else if(depth>0){
	    		if(ZMonitor.isMonitoring()){
	    			record(event, depth, lfc, ndcStr);
	    		}else if(isIgnitBySelf ||  testMode){
	    			// if the ms life-cycle is controlled by others, log4j should never take over the control.   
	    			// be cause we'd never know if log4j's logging methods will be called inside a managed thread. 
	    			start(event, depth, lfc, ndcStr);
	    			setControlledBySelf(lfc);
	    		}
	    	}
	    	
	    }
	}
	
	private static final String KEY_CONTROLLED_BY_SELF = "KEY_CONTROLLED_BY_SELF";
	
	private static boolean isControlledBySelf(MonitorLifecycle lfc){
		return lfc.getAttribute(KEY_CONTROLLED_BY_SELF)!=null;
	}
	private static void setControlledBySelf(MonitorLifecycle lfc){
		lfc.setAttribute(KEY_CONTROLLED_BY_SELF, KEY_CONTROLLED_BY_SELF);
	}
	
	private static final String KEY_NDC_CTXT = "KEY_"+NdcContext.class;
	private static NdcContext getNdcContext(MonitorLifecycle lfc){
		NdcContext stack = lfc.getAttribute(KEY_NDC_CTXT);
		if(stack==null){
			lfc.setAttribute(KEY_NDC_CTXT, 
					stack = new NdcContext(lfc));
		}
		return stack;
	}
	
	/**
	 * 
	 * @param event
	 * @param depth
	 * @param lfc
	 * @param ndcStr
	 */
	private void start(LoggingEvent event, int depth, MonitorLifecycle lfc,
			String ndcStr) {
		NdcContext ndcCtxt = getNdcContext(lfc);
		
//		if(ndcCtxt.getNdcObj()!=null){
//			NdcObj obj = ndcCtxt.getNdcObj();
//			throw new IllegalStateException(
//				"ZMonitor Log4j stack Operation Logic Error or forget to cleanup the state.");			
//		}

		ndcCtxt.doStart(newTrackingContext(event, ndcStr), ndcStr, depth);
//		System.out.printf("ZMonitorAppender::start() ndcStr=%1$2s ,NdcObj=%2$2s, isMonitorStarted=%3$b\n" ,
//				ndcStr, ndcCtxt.getNdcObj(), lfc.isMonitorStarted());
	}

	/**
	 *  Developer will use log4j's: NDC.push() & NDC.pop()
	 *  to command ZMonitor to do: tl.start() & tl.end()
	 *  
	 *  ASSUMPTION: 
	 *  	user wont use ZMonitor directly while using log4j NDC, 
	 *  	the current tl.depth will always match to the last NDC state
	 *  	we may consider the direct operation of MonitorSequence in the future.
	 *  
	 *  Base on this concept:
	 *  
	 *  If: current NDC Depth > last NDC Depth( or there's no last NDC Depth)
	 *  	we need to do tl.start(), and push current NDC Depth.
	 *  	(check : if currentTlDepth != lastTlDepth do fail-over.)
	 *  
	 *  If: current NDC Depth = last NDC Depth
	 *  	we simply do tl.record().
	 *  
	 *  If: current NDC Depth < last NDC Depth
	 *   	if: current NDC Depth = last.last NDC Depth
	 *      	We call tl.end() and pop NDC stack, because user is telling 
	 *      	us he want to end the current stack.
	 *      	(check : if currentTlDepth != lastTlDepth do fail-over.)
	 *      
	 *      if: current NDC Depth >  last.last NDC Depth
	 *      	We simply do tl.record() because it's not reach the end yet.
	 *      	(check : if currentTlDepth != lastTlDepth do fail-over.)
	 *      	 
	 *      if: current NDC Depth <  last.last NDC Depth
	 *      	We recursively do tl.end with Pop NDCStack and renew the last NDC depth 
	 *      	till this condition is not satisfied.
	 *      	  
	 *  
	 * @param event
	 * @param message
	 * @param ndcDepth
	 * @param ndcStr 
	 */
	private void record(LoggingEvent event, int ndcDepth, MonitorLifecycle lfc, String ndcStr){
		
		// condition 1. MonitorSequence already started.
		
		
		NdcContext ndcCtxt = getNdcContext(lfc);
		NdcObj last = ndcCtxt.getNdcObj();
		
//		if(last.tlDepth != getCurrentTlDepth(lfc)){
//			//TODO:MonitorSequence is operated between two LOG4J log commands, the stack information might be fucked up!   
//			/*
//			 * Should I allow user to mix log4j with native ZMonitor API?  
//			 */
//		}
		
//		System.out.printf("ZMonitorAppender::record() ndcStr=%1$2s ,NdcObj=%2$2s \n" ,
//				ndcStr, last);
		
		if(last==null){
			ndcCtxt.doRecord(newTrackingContext(event, null), ndcDepth);
			return;
		}
		
		if(ndcDepth > last.depth){
			ndcCtxt.doStart(newTrackingContext(event, null), ndcStr, ndcDepth);
			
		}else if(ndcDepth == last.depth){
			ndcCtxt.doRecord(newTrackingContext(event, null), ndcDepth);
			
		}else{//if( ndcDepth < last.depth )
			if(ndcDepth == last.previous.depth){
				ndcCtxt.doEnd(newTrackingContext(event, "LOG4J_END"));
				
			}else if(ndcDepth > last.previous.depth){
				ndcCtxt.doRecord(newTrackingContext(event, null), ndcDepth);
				
			}else{// if(ndcDepth < last.previous.depth)
				autoEnd(ndcCtxt, event);
				record(event, ndcDepth, lfc, ndcStr);//recursive call...
				return;
			}
		}
	}

	/**
	 * 
	 * @param finalName
	 * @param finalMesg
	 */
	private void complete(LoggingEvent event, String finalMesg, MonitorLifecycle lfc){

		NdcContext ndcCtxt = getNdcContext(lfc);
		
		int currentTlDepth = getCurrentTlDepth(lfc);
		
//		System.out.printf("ZMonitorAppender::complete() currentTlDepth=%1$2s ,NdcObj=%2$2s \n" ,
//				currentTlDepth, ndcCtxt.getNdcObj());
		
		while(currentTlDepth>1){
			autoEnd(ndcCtxt, event);
			currentTlDepth = getCurrentTlDepth(lfc);
		}
		TrackingContext jName = newTrackingContext(event, "END");
		ndcCtxt.doEnd(jName);
	}
	
	private void autoEnd(NdcContext ndcCtxt, LoggingEvent event){
		NdcObj last = ndcCtxt.getNdcObj();
		
		StringBuffer sb = new StringBuffer();
		Strings.append(sb, "ndc[",last.depth,"|",
				(last==null? 0 : last.ndcStr),
				"], recursive Ending...");
		
		ndcCtxt.doEnd(newTrackingContext(event, "L4J_FORCE_END", 
				sb.toString()));
	}
	
	
	/**
	 * 
	 * @return -1 if MonitorSequence is not initialized,  
	 */
	protected static int getCurrentTlDepth(MonitorLifecycle lfc){
		
		MonitorSequence tl = lfc.getMonitorSequence();
		return (tl==null)? -1 : tl.getCurrentDepth();
	}
	
	/**
	 * 
	 * @param event
	 * @param markerName
	 * @param message
	 * @return
	 */
	protected TrackingContext newTrackingContext(LoggingEvent event, String markerName, String message){
		TrackingContextBase ctx = new TrackingContextBase("log4j");
		ctx.setMessage(message);
		if (javaSourceLocationInfo) {
			LocationInfo locInfo = event.getLocationInformation();
			
			
			int lineNum = -1;
			try {
				lineNum = Integer.parseInt(locInfo.getLineNumber());
			} catch (Exception e) {
			}// line number is not applicable, ignore it.
			Marker marker = markerName==null? 
					null:MarkerFactory.getMarker(markerName);
			
			MonitorMetaBase cInfo = new MonitorMetaBase(marker,
					getMonitorPointNameType(),
					locInfo.getClassName(), 
					locInfo.getMethodName(), 
					lineNum, null);
			ctx.setMonitorMeta(cInfo);
		} else {
			MonitorMetaBase cInfo = new MonitorMetaBase();
			cInfo.setClassName(event.getLoggerName());
			ctx.setMonitorMeta(cInfo);
		}
		return ctx;
	}
	/**
	 * 
	 * @param event
	 * @param markerName
	 * @return
	 */
	protected TrackingContext newTrackingContext(LoggingEvent event, String markerName) {
		return newTrackingContext(event, markerName, event.getRenderedMessage());
	}
	
	/**
	 * used to prevent recursion appender process. 
	 * @param claz
	 * @param loggerName
	 * @return
	 */
	protected static boolean preventRecursion(Class<?> claz, String loggerName){
		return Logger.getLogger(claz).getName().equals(loggerName);
	}
	

	
}
