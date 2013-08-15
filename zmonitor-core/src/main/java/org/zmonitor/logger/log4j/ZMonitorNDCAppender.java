/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;
import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.log4j.NdcContext.NdcObj;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.util.Strings;

/**
 * 
 * an experimental zmonitor tracker implementation  to support log4j NDC.
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorNDCAppender extends ZMonitorAppenderBase {

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
	    				tracking(event, depth, lfc, ndcStr);
	    			}
				}
	    		// do nothing...
	    		//tl.start must satisfy (depth > 0), otherwise there's no way for appender to know when to complete MonitorSequence.
	    	}else if(depth>0){
	    		if(ZMonitor.isMonitoring()){
	    			tracking(event, depth, lfc, ndcStr);
	    		}else if(isInitByAppender ||  inTestMode){
	    			// if the ms life-cycle is controlled by others, log4j should never take over the control.   
	    			// be cause we'd never know if log4j's logging methods will be called inside a managed thread. 
	    			start(event, depth, lfc, ndcStr);
	    			setControlledBySelf(lfc);
	    		}
	    	}
	    	
	    }
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

		ndcCtxt.doStart(newTrackingContext(event, Markers.MK_PUSH_LOG4J, event.getThreadName()), ndcStr, depth);
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
	private void tracking(LoggingEvent event, int ndcDepth, MonitorLifecycle lfc, String ndcStr){
		
		// condition 1. MonitorSequence already started.
		
		
		NdcContext ndcCtxt = getNdcContext(lfc);
		NdcObj last = ndcCtxt.getNdcObj();
		String threadId = event.getThreadName();
		
//		if(last.tlDepth != getCurrentTlDepth(lfc)){
//			//TODO:MonitorSequence is operated between two LOG4J log commands, the stack information might be fucked up!   
//			/*
//			 * Should I allow user to mix log4j with native ZMonitor API?  
//			 */
//		}
		
//		System.out.printf("ZMonitorAppender::record() ndcStr=%1$2s ,NdcObj=%2$2s \n" ,
//				ndcStr, last);
		
		if(last==null){
			ndcCtxt.doRecord(newTrackingContext(event, Markers.MK_RECORD_LOG4J, threadId), ndcDepth);
			return;
		}
		
		if(ndcDepth > last.depth){
			ndcCtxt.doStart(newTrackingContext(event, Markers.MK_PUSH_LOG4J, threadId), ndcStr, ndcDepth);
			
		}else if(ndcDepth == last.depth){
			ndcCtxt.doRecord(newTrackingContext(event, Markers.MK_RECORD_LOG4J, threadId), ndcDepth);
			
		}else{//if( ndcDepth < last.depth )
			if(ndcDepth == last.previous.depth){
				ndcCtxt.doEnd(newTrackingContext(event, Markers.MK_END_LOG4J, threadId));
				
			}else if(ndcDepth > last.previous.depth){
				ndcCtxt.doRecord(newTrackingContext(event, Markers.MK_RECORD_LOG4J, threadId), ndcDepth);
				
			}else{// if(ndcDepth < last.previous.depth)
				autoEnd(ndcCtxt, event);
				tracking(event, ndcDepth, lfc, ndcStr);//recursive call...
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
		TrackingContext jName = newTrackingContext(event, Markers.MK_END_LOG4J, event.getThreadName());
		ndcCtxt.doEnd(jName);
	}
	
	private void autoEnd(NdcContext ndcCtxt, LoggingEvent event){
		NdcObj last = ndcCtxt.getNdcObj();
		
		StringBuffer sb = new StringBuffer();
		Strings.append(sb, "ndc[",last.depth,"|",
				(last==null? 0 : last.ndcStr),
				"], recursive Ending...");
		
		ndcCtxt.doEnd(newTrackingContext(event, Markers.MK_END_LOG4J, 
				sb.toString()));
	}
	
	
	/**
	 * 
	 * @return -1 if MonitorSequence is not initialized,  
	 */
	protected static int getCurrentTlDepth(MonitorLifecycle lfc){
		
		MonitorSequence tl = lfc.getMonitorSequence();
		return (tl==null)? -1 : lfc.getStack().getCurrentDepth();
	}
	
	protected static final String KEY_CONTROLLED_BY_SELF = "KEY_CONTROLLED_BY_SELF";
	
	protected static boolean isControlledBySelf(MonitorLifecycle lfc){
		return lfc.getAttribute(KEY_CONTROLLED_BY_SELF)!=null;
	}
	protected static void setControlledBySelf(MonitorLifecycle lfc){
		lfc.setAttribute(KEY_CONTROLLED_BY_SELF, KEY_CONTROLLED_BY_SELF);
	}
	
}
