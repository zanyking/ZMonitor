/**
 * 
 */
package org.zmonitor.logger.logback;

import org.zmonitor.TrackingContext;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.logger.LoggerMonitorMeta;
import org.zmonitor.logger.MessageTuple;
import org.zmonitor.logger.TrackerBase;
import org.zmonitor.logger.log4j.Markers;
import org.zmonitor.marker.Marker;
import org.zmonitor.util.CallerStackTraceElementFinder;

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
	private static final CallerStackTraceElementFinder ST_ELEMENT_FINDER = 
			new CallerStackTraceElementFinder(
					"java.lang",
					"org.zmonitor",
					"java.util",
					"ch.qos.logback",
					"org.slf4j");
	
	private TrackingContext newTrackingContext(ILoggingEvent event){
		TrackingContextBase ctx = new TrackingContextBase(Markers.TRACKER_NAME_LOG4J);
		ctx.setMessage(newMessage(event));
		LoggerMonitorMeta lmMeta = new LoggerMonitorMeta(
				transform(event.getMarker()), 
			Markers.TRACKER_NAME_LOG4J, 
			ST_ELEMENT_FINDER.find(event.getCallerData()), 
			event.getLevel().toString());
		
		event.getTimeStamp();
		ctx.setMonitorMeta(lmMeta);
		return ctx;
	}
	
	
	private static MessageTuple newMessage(ILoggingEvent event){
		MessageTuple mt = new MessageTuple(
			event.getMessage(), event.getArgumentArray(), null);
		return mt;
	}
	
	
	private Marker transform(org.slf4j.Marker slf4jMk){
		//TODO: transform slf4j Marker to ZMonitor Marker.
		return null;
	}
}
