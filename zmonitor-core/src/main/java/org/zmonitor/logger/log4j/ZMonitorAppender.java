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
import org.zmonitor.IgnitionFailureException;
import org.zmonitor.Ignitor;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.JavaName;
import org.zmonitor.impl.StringName;
import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.impl.CoreConfigurator;
import org.zmonitor.impl.XmlConfiguratorLoader;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.log4j.NdcContext.NdcObj;
import org.zmonitor.spi.Name;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorAppender extends AppenderSkeleton {

	protected boolean locationInfo = true;
	protected String measurePointNameType = "LOG4J";

	private static ZMonitorAppender singleton;
	
	public ZMonitorAppender(){
		this.name = "ZMonitor";
		synchronized (ZMonitorAppender.class) {
			if(singleton ==null)
				singleton = this;
		}
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
		return locationInfo;
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
		locationInfo = flag;
	}
	/**
	 * 
	 * @return
	 */
	public String getMeasurePointNameType() {
		return measurePointNameType;
	}
	/**
	 * 
	 * @param measurePointNameType
	 */
	public void setMeasurePointNameType(String measurePointNameType) {
		this.measurePointNameType = measurePointNameType;
	}


	public boolean requiresLayout() {
		return false;
	}
	
	private boolean isIgnitBySelf;
	@Override
	public void activateOptions() {
		super.activateOptions();
		if(ZMonitorManager.isInitialized())return;
		try {
			//TODO create configuration Source...
			final CoreConfigurator xmlCofig = XmlConfiguratorLoader.loadForPureJavaProgram();
			if(xmlCofig==null)
				throw new IgnitionFailureException("cannot find Configuration:["+
						XmlConfiguratorLoader.ZMONITOR_XML+
						"] from current application context: "+this.getClass());
			ZMonitorManager aZMonitorManager = new ZMonitorManager();
			aZMonitorManager.setLifecycleManager(new ThreadLocalMonitorSequenceLifecycleManager());
			ZMonitorManager.init(aZMonitorManager);
			isIgnitBySelf = true;
			ZMLog.info(">> Ignit ZMonitor in: ",ZMonitorAppender.class.getCanonicalName());
		} catch (IOException e) {
			throw new IgnitionFailureException(e);
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
	    
	    if(shallRecursionPrevented(this.getClass(), event.getLoggerName()) || 
	    		shallRecursionPrevented(ZMLog.class, event.getLoggerName()))
	    	return;
	    
	    
	    {//IMPORTANT: this section is something MUST be called!
	    	event.getRenderedMessage();
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
	    	 *  depth>0, tl!=started, controlByOthers	 [do Start] 
	    	 *  
	    	 */
	    	MonitorSequenceLifecycle lfc = ZMonitorManager.getInstance().getMonitorSequenceLifecycle();
	    	
			String mesg = event.getRenderedMessage();
	    	if(depth==0){
	    		if(ZMonitor.isMonitoring()){
	    			if(isControlledBySelf(lfc)){
	    				complete(event, mesg, lfc);
	    			}else{
	    				record(event, depth, lfc, ndcStr);
	    			}
				}
	    		// do nothing...
	    		//tl.start must satisfy (depth > 0), otherwise there's no way for appender to know when to complete timeline.
	    	}else{
	    		if(ZMonitor.isMonitoring()){
	    			record(event, depth, lfc, ndcStr);
	    		}else{
	    			start(event, depth, lfc, ndcStr);
	    			setControlledBySelf(lfc);
	    		}
	    	}
	    }
	}
	
	private static final String KEY_CONTROLLED_BY_SELF = "KEY_CONTROLLED_BY_SELF";
	private static boolean isControlledBySelf(MonitorSequenceLifecycle lfc){
		return lfc.getAttribute(KEY_CONTROLLED_BY_SELF)!=null;
	}
	private static void setControlledBySelf(MonitorSequenceLifecycle lfc){
		lfc.setAttribute(KEY_CONTROLLED_BY_SELF, KEY_CONTROLLED_BY_SELF);
	}
	
	private static final String KEY_NDC_CTXT = "KEY_"+NdcContext.class;
	private static NdcContext getNdcContext(MonitorSequenceLifecycle lfc){
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
	private void start(LoggingEvent event, int depth, MonitorSequenceLifecycle lfc,
			String ndcStr) {
		NdcContext ndcCtxt = getNdcContext(lfc);
		if(ndcCtxt.getNdcObj()!=null)
			throw new IllegalStateException("ZMonitor Log4j stack Operation Logic Error or forget to cleanup the state.");
		ndcCtxt.doStart(createName(event, null), event.getRenderedMessage(), ndcStr, depth);
	}

	/**
	 *  Developer will use log4j's: NDC.push() & NDC.pop()
	 *  to command ZMonitor to do: tl.start() & tl.end()
	 *  
	 *  ASSUMPTION: 
	 *  	user wont use ZMonitor directly while using log4j NDC, 
	 *  	the current tl.depth will always match to the last NDC state
	 *  	we may consider the direct operation of timeline in the future.
	 *  
	 *  Base on this concept:
	 *  
	 *  If: current NDC Depth > last NDC Depth( or there's no last NDC Depth)
	 *  	we need to do tl.start(), and push current NDC Depth.
	 *  	(check : if currentTlDepth != lastTlDepth do failover.)
	 *  
	 *  If: current NDC Depth = last NDC Depth
	 *  	we simply do tl.record().
	 *  
	 *  If: current NDC Depth < last NDC Depth
	 *   	if: current NDC Depth = last.last NDC Depth
	 *      	We call tl.end() and pop NDC stack, because user is telling 
	 *      	us he want to end the current stack.
	 *      	(check : if currentTlDepth != lastTlDepth do failover.)
	 *      
	 *      if: current NDC Depth >  last.last NDC Depth
	 *      	We simply do tl.record() because it's not reach the end yet.
	 *      	(check : if currentTlDepth != lastTlDepth do failover.)
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
	private void record(LoggingEvent event, int ndcDepth, MonitorSequenceLifecycle lfc, String ndcStr){
		
		//TODO: how to figure out this part?
		// condition 1. Timeline already started.
		
		
		NdcContext ndcCtxt = getNdcContext(lfc);
		NdcObj last = ndcCtxt.getNdcObj();
		
//		if(last.tlDepth != getCurrentTlDepth(lfc)){
//			//TODO:Timeline is operated between two LOG4J log commands, the stack information might be fucked up!   
//			/*
//			 * Should I allow user to mix log4j with native ZMonitor API?  
//			 */
//		}
		
		
		String mesg = event.getRenderedMessage();
		if(last==null){
			ndcCtxt.doRecord(createName(event, null), 
					mesg, ndcDepth);
			return;
		}
		
		if(ndcDepth > last.depth){
			ndcCtxt.doStart(createName(event, null), mesg, ndcStr, ndcDepth);
			
		}else if(ndcDepth == last.depth){
			ndcCtxt.doRecord(createName(event, null), mesg, ndcDepth);
			
		}else{//if( ndcDepth < last.depth )
			if(ndcDepth == last.previous.depth){
				ndcCtxt.doEnd(createName(event, "L4J_END"), 
						mesg);
				
			}else if(ndcDepth > last.previous.depth){
				ndcCtxt.doRecord(createName(event, null), 
						mesg, ndcDepth);
				
			}else{// if(ndcDepth < last.previous.depth)
				autoEnd(ndcCtxt);
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
	private void complete(LoggingEvent event, String finalMesg, MonitorSequenceLifecycle lfc){
		Name jName = createName(event, "END");
		
		NdcContext ndcCtxt = getNdcContext(lfc);
		
		int currentTlDepth = getCurrentTlDepth(lfc);
		while(currentTlDepth>1){
			autoEnd(ndcCtxt);
			currentTlDepth = getCurrentTlDepth(lfc);
		}
		ndcCtxt.doEnd(jName, finalMesg);
	}
	
	private static void autoEnd(NdcContext ndcCtxt ){
		NdcObj last = ndcCtxt.getNdcObj();
		
		StringBuffer sb = new StringBuffer();
		Strings.append(sb, "dnc[",last.depth,"|",
				(last==null? 0 : last.ndcStr),
				"], recursive Ending...");
		
		ndcCtxt.doEnd(new StringName("L4J_FORCE_END"), sb.toString());
	}
	
	
	/**
	 * 
	 * @return -1 if timeline is not initialized,  
	 */
	protected static int getCurrentTlDepth(MonitorSequenceLifecycle lfc){
		
		MonitorSequence tl = lfc.getMonitorSequence();
		return (tl==null)? -1 : tl.getCurrentDepth();
	}
	
	/**
	 * 
	 * @param event
	 * @return
	 */
	protected Name createName(LoggingEvent event, String name) {
		JavaName jName = new JavaName(name==null ? measurePointNameType : name);
		if (locationInfo) {
			LocationInfo locInfo = event.getLocationInformation();
			jName.setClassName(locInfo.getClassName());
			jName.setMethodName(locInfo.getMethodName());
			Integer lineNum = null;
			try {
				lineNum = Integer.parseInt(locInfo.getLineNumber());
			} catch (Exception e) {
			}// line number is not applicable, ignore it.
			if (lineNum != null)
				jName.setLineNumber(lineNum);
		} else {
			jName.setClassName(event.getLoggerName());
		}
		return jName;
	}
	
	/**
	 * used to prevent recursion appender process. 
	 * @param claz
	 * @param loggerName
	 * @return
	 */
	protected static boolean shallRecursionPrevented(Class<?> claz, String loggerName){
		return Logger.getLogger(claz).getName().equals(loggerName);
	}

	
}
