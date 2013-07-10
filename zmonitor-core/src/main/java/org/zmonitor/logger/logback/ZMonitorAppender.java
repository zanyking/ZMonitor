/**
 * 
 */
package org.zmonitor.logger.logback;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.zmonitor.MonitorMeta;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.logger.LoggerMonitorMeta;
import org.zmonitor.logger.MessageTuple;
import org.zmonitor.logger.TrackerBase;
import org.zmonitor.marker.IMarkerFactory;
import org.zmonitor.marker.Marker;
import org.zmonitor.util.StackTraceElementFinder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorAppender extends AppenderBase<ILoggingEvent>{
	

	@Override
	protected void append(ILoggingEvent event) {
		if (!isStarted()) {
			return;
		}
		//TODO: perform ZMonitor Tracking here.
		try{
			getTracker().doTrack(newTrackingContext(event));	
		}catch(Throwable  e){
			e.printStackTrace();
		}
	}
	private TrackerBase tracker; 
	private TrackerBase getTracker(){
		if(tracker==null){
			tracker = LogbackConfigurator.getInstance().getTracker();
		}
		return tracker; 
	}
	private static final StackTraceElementFinder ST_ELEMENT_FINDER = 
			new StackTraceElementFinder(
					"java.lang",
					"org.zmonitor",
					"java.util",
					"ch.qos.logback",
					"org.slf4j");
	
	private TrackingContext newTrackingContext(ILoggingEvent event){
		StackTraceElement[] elements = event.getCallerData();
		
		final LoggerMonitorMeta lmMeta = new LoggerMonitorMeta(
				transform(event.getMarker()), 
			Markers.TRACKER_NAME_LOGBACK, 
			elements==null? null:elements[0], 
			event.getLevel().toString());
		
		TrackingContextBase ctx = new TrackingContextBase(Markers.TRACKER_NAME_LOGBACK, elements){
			public MonitorMeta newMonitorMeta() {
				return lmMeta;
			}
		};
		ctx.setCreateMillis(event.getTimeStamp());
		ctx.setMessage(newMessage(event));
		return ctx;
	}
	
	
	private static MessageTuple newMessage(ILoggingEvent event){
		
		MessageTuple mt = new MessageTuple(
			event.getMessage(), event.getArgumentArray(), null);
		return mt;
	}
	
	
	
	private Marker transform(org.slf4j.Marker slf4jMk){
		//TODO: transform slf4j Marker to ZMonitor Marker.
		// ignore original slf4j marker?
		return null;
	}
}
/**
 * Not done yet, this is not that important by now, I should do it latter.
 * @author Ian YT Tsai(Zanyking)
 *
 */
class MarkerCache{

	
	private static class State{
		org.slf4j.Marker parent;
		void a(){
//			Iterators.
		}
	}
	
	/**
	 * 
	 * @param slf4jMk
	 * @return
	 */
	public Marker get(org.slf4j.Marker slf4jMk){
		String mkName = slf4jMk.getName();
		IMarkerFactory mgFac = ZMonitorManager.getInstance().getMarkerFactory();
		if(mgFac.exists(mkName) || !slf4jMk.hasReferences()){
			return mgFac.getMarker(mkName); 
		}
		
		//init stack
		LinkedList<org.slf4j.Marker> sl_Stack = 
				new LinkedList<org.slf4j.Marker>();
		
		pushAllKids(slf4jMk, sl_Stack);
		
		Marker zRoot = mgFac.getMarker(mkName);//parent
		Marker zMk = zRoot;
		Marker zTemp;
		org.slf4j.Marker sl_Mk;
		boolean exist;
		
		while(!sl_Stack.isEmpty()){//BFS
			sl_Mk = sl_Stack.pop();
			mkName = sl_Mk.getName();
			exist = mgFac.exists(mkName);
			zTemp = mgFac.getMarker(mkName);
			zMk.add(zTemp);
			if(!exist && sl_Mk.hasReferences()){// add kids to stack
				Iterator<org.slf4j.Marker> sl_itor = sl_Mk.iterator();
				while(sl_itor.hasNext()){
					sl_Mk = sl_itor.next();
				}
			}
			
		}
		return zRoot;
	}
	
	private static void pushAllKids(org.slf4j.Marker parent, LinkedList<org.slf4j.Marker> stack){
		Iterator<org.slf4j.Marker> sl_itor = parent.iterator();
		while(sl_itor.hasNext()){
			stack.push(sl_itor.next());
		}
	}
	
}



