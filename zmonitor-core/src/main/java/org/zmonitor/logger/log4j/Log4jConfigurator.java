
/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.DefaultMPVariableResolver;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.logger.AppenderConfiguratorBase;
import org.zmonitor.logger.AppenderTracker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4jConfigurator extends AppenderConfiguratorBase {
	
	private static final String REL_LOG4J_CONF = "log4j-conf";
	
	public Log4jConfigurator() {
		super(REL_LOG4J_CONF, 
				REL_LOG4J_CONF, 
				new AppenderTracker(),
				"org.zmonitor.logger.log4j.Driver");
		tracker.setPushMarcker(Markers.MK_PUSH_LOG4J);
		tracker.setPopMarcker(Markers.MK_END_LOG4J);
		tracker.setTrackingMarcker(Markers.MK_RECORD_LOG4J);
	}
	
	static Log4jConfigurator getInstance(){
		return ZMonitorManager.getInstance().getBeanIfAny(Log4jConfigurator.class);
	}

	protected void configureDefault(ZMonitorManager manager) {
		manager.getSelectorAdaptor().addSupport(Markers.TRACKER_NAME_LOG4J, 
				new DefaultSelectorAdaptation() {
			public String retrieveType(MonitorPoint mp) {
				return Markers.retrieveRegisteredMajorMarker(mp.getMonitorMeta()).getName();
			}
			public Object resolveAttribute(String varName, MonitorPoint mp) {
				return new DefaultMPVariableResolver(mp).resolveVariable(varName);
			}
		});		
	}

//	
//	/*
//	 * 
//	 */
//	protected void prepareCustomAppender(boolean autoHookUp, 
//			ConfigContext appenderCtx) {
//		
//		// TODO: prepare detailed appender settings here, appender will gather
//		// these info while working. 
//		
//		if(!autoHookUp)return;
//		
//		try {
//			Driver.hookUpCustomAppender( appenderCtx);
//			
//		} catch (NoClassDefFoundError e) {// NoClassDefFoundError:
//								// org/apache/log4j/AppenderSkeleton
//			e.printStackTrace();
//			ZMLog.info("cannot init Custom Appender for Log4j, " +
//					"application doesn't use log4j.");
//		}
//		
//	}
	
}
