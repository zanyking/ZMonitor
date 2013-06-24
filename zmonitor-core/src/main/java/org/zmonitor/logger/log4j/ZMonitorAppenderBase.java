/**
 * 
 */
package org.zmonitor.logger.log4j;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.zmonitor.AlreadyStartedException;
import org.zmonitor.InitFailureException;
import org.zmonitor.MarkerFactory;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.marker.Marker;
import org.zmonitor.spi.MonitorLifecycle;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ZMonitorAppenderBase extends AppenderSkeleton{
	protected boolean javaSourceLocationInfo = true;
	
	
	private static ZMonitorAppenderBase singleton;
	
	public ZMonitorAppenderBase(){
		this.name = "ZMonitor";
		synchronized (ZMonitorNDCAppender.class) {
			if(singleton ==null)
				singleton = this;
		}
	}
	protected boolean testMode;
	
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
	public static ZMonitorAppenderBase getInstance(){
		return singleton;
	}
	/**
	 * Gets whether the location of the logging request call should be captured.
	 * 
	 * @since 1.2.16
	 * @return the current value of the <b>LocationInfo</b> option.
	 */
	public boolean isLocationInfo() {
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
	

	
	public boolean requiresLayout() {
		return false;
	}
	protected boolean isIgnitBySelf;

	/*
	 * Sub class should never interfere this part, no guarantee for another
	 * log4j appender who play the initiator for ZMonitorManager in the same 
	 * application.
	 */
	public final void activateOptions() {
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
			ZMLog.info(">> Ignite ZMonitor in: ",ZMonitorAppenderBase.class.getCanonicalName());
		} catch (IOException e) {
			throw new InitFailureException(e);
		} catch (AlreadyStartedException e) {
			ZMLog.info("ZMonitorManager is already initialized");
		}
	}
	/**
	 * 
	 * @param event
	 * @param markerName
	 * @param message
	 * @return
	 */
	protected TrackingContext newTrackingContext(LoggingEvent event, String markerName, String message){
		TrackingContextBase ctx = new TrackingContextBase(Markers.TRACKER_NAME_LOG4J);
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
					ctx.getTrackerName(),
					locInfo.getClassName(), 
					locInfo.getMethodName(), 
					lineNum, 
					locInfo.getFileName());
			ctx.setMonitorMeta(cInfo);
		} else {
			MonitorMetaBase cInfo = new MonitorMetaBase();
			cInfo.setClassName(event.getLoggerName());
			ctx.setMonitorMeta(cInfo);
		}
		return ctx;
	}
	public void close() {
		if(isIgnitBySelf){
			ZMonitorManager.dispose();
		}
	}
	
	protected static final String KEY_CONTROLLED_BY_SELF = "KEY_CONTROLLED_BY_SELF";
	
	protected static boolean isControlledBySelf(MonitorLifecycle lfc){
		return lfc.getAttribute(KEY_CONTROLLED_BY_SELF)!=null;
	}
	protected static void setControlledBySelf(MonitorLifecycle lfc){
		lfc.setAttribute(KEY_CONTROLLED_BY_SELF, KEY_CONTROLLED_BY_SELF);
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
