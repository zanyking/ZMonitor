
/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.impl.DefaultMPVariableResolver;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.ConfiguratorBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4jConfigurator extends ConfiguratorBase {
	
	private static final String REL_LOG4J_CONF = "log4j-conf";
	private static final String REL_APPENDER = "appender";
	
	public Log4jConfigurator() {
		super(REL_LOG4J_CONF, REL_LOG4J_CONF, new AppenderTracker());
		tracker.setPushMarcker(Markers.MK_PUSH_LOG4J);
		tracker.setPopMarcker(Markers.MK_END_LOG4J);
		tracker.setTrackingMarcker(Markers.MK_RECORD_LOG4J);
	}
	
	public static Log4jConfigurator getInstance(){
		return ZMonitorManager.getInstance().getBeanIfAny(Log4jConfigurator.class);
	}

	@Override
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

	@Override
	protected void configureByContext(ConfigContext log4jConfCtx) {
		/*
		 * <appender class="org.zmonitor.logger.log4j.ZMonitorAppender">
		 * the given class must extends org.zmonitor.logger.log4j.ZMonitorAppenderBase.
		 */
		prepareLog4jCustomAppender(autoHookUp, 
				log4jConfCtx.toNode(REL_APPENDER));
	}
	/*
	 * 
	 */
	private static void prepareLog4jCustomAppender(boolean autoHookUp, 
			ConfigContext appenderCtx) {
		
		// TODO: prepare detailed appender settings here, appender will gather
		// these info while working. 
		
		if(!autoHookUp)return;
		
		try {
			Driver.hookUpCustomAppender( appenderCtx);
		} catch (NoClassDefFoundError e) {// NoClassDefFoundError:
								// org/apache/log4j/AppenderSkeleton
			e.printStackTrace();
			ZMLog.info("cannot init Custom Appender for Log4j, " +
					"application doesn't use log4j.");
		}
		
	}


	private boolean autoHookUp;
	/**
	 * 
	 * @return
	 */
	public boolean isAutoHookUp() {
		return autoHookUp;
	}
	
	/**
	 * <p>
	 * If you want ZMonitor to add a custom appender to your application's log4j
	 * context while ZMonitor initialization. Configure this setting to true in 
	 * <i>zmonitor.xml/log4j-conf:autoHookUp</i>. This mechanism is designed for
	 * Developer who don't want to switch log4j configurations between development 
	 * environment  and production environment.
	 * 
	 * <p>
	 * If you want to do further detailed control to ZMonitorAppender, or
	 * you want to use ZMonitor in your production environment, please
	 * considering of declaring the {@code org.zmonitor.logger.log4j.ZMonitorAppender} 
	 * manually in your application's log4j setting.
	 * 
	 * <p>
	 * <b>NOTICE:</b><br>
	 * Due to class loading sequence and module initialization ordering, 
	 * Auto hook up can only worked while ZMonitorManager is initialized by
	 * others (e.g. {@link org.zmonitor.web.ZMonitorServletFilter}), and log4j
	 * Factory is already started.
	 * @param autoHookUp
	 *            set to true to ask ZMonitor to insert a custom appender to
	 *            your application's log4j context, default value is
	 *            <i>false</i>
	 */
	public void setAutoHookUp(boolean autoHookUp) {
		this.autoHookUp = autoHookUp;
	}

	
}
