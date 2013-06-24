/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.WrongConfigurationException;
import org.zmonitor.impl.DefaultMPVariableResolver;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4jConfigurator extends ZMBeanBase implements Configurator {
	
	private static final String REL_LOG4J_CONF = "log4j-conf";
	private static final String REL_APPENDER = "appender";
	private static final String ID_ZMBEAN_LOG4J_CONF = REL_LOG4J_CONF;
	
	public Log4jConfigurator() {
		this.setId(ID_ZMBEAN_LOG4J_CONF);
	}

	public void configure(ConfigContext configCtx) {
		ZMonitorManager manager = configCtx.getManager();
		manager.getSelectorAdaptor().addSupport(Markers.TRACKER_NAME_LOG4J, 
				new DefaultSelectorAdaptation() {
			public String retrieveType(MonitorPoint mp) {
				return Markers.retrieveRegisteredMajorMarker(mp.getMonitorMeta()).getName();
			}
			public Object resolveAttribute(String varName, MonitorPoint mp) {
				return new DefaultMPVariableResolver(mp).resolveVariable(varName);
			}
		});
		ConfigContext log4jConfCtx = configCtx.toNode(REL_LOG4J_CONF);
		if (log4jConfCtx.getNode() == null) {// no <log4j-conf> element has been
												// found in zmonitor.xml, use
												// default setting.
			return;
		}
		log4jConfCtx.applyAttributes(new PropertySetter(this), "class");
		prepareLog4jCustomAppender(autoHookUp, appenderClass, log4jConfCtx.toNode(REL_APPENDER));
	}
	/*
	 * 
	 */
	private static void prepareLog4jCustomAppender(boolean autoHookUp, 
			Class<?> appenderClz,
			ConfigContext appenderCtx) {
		if(!autoHookUp)return;
		Logger root = Logger.getRootLogger();
		ZMonitorAppenderBase log4jAppender;
		if(appenderCtx.getNode()==null){
			//use default setting... 
			log4jAppender = new ZMonitorAppender();
			
		}else{
			//use custom setting...
			//TODO: more detailed configuration of custom log4j appender.
			
			// must extends ZMonitorAppenderBase...
			log4jAppender = appenderCtx.newBean(ZMonitorAppender.class, false);
		}
		root.addAppender(log4jAppender);
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
	
	
	private Class<?> appenderClass = ZMonitorAppender.class;
	public String getAppenderClass() {
		return appenderClass.getName();
	}

	/**
	 * <p>
	 * if autoHookUp is true, this class will be used to create the appender
	 * instance.
	 * 
	 * @param appenderClassName
	 */
	public void setAppenderClass(String appenderClassName) {
		Class<?> temp;
		try {
			Arguments.checkNotEmpty(appenderClassName,"appenderClassName cannot be set to empty!");
			temp = Class.forName(appenderClassName);
		} catch (Exception e) {
			throw new WrongConfigurationException(
					"Appender class has not been found: " + appenderClassName,
					e);
		}
		try{
			temp.getConstructor();
		}catch (Exception e) {
			throw new WrongConfigurationException(
					"no default constructor of appender classes implementation: " + appenderClassName,
					e);
		}
		this.appenderClass = temp;
	}
	
	
}
