/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.InitFailureException;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.logger.Driver.HookUpContext;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class AppenderConfiguratorBase extends ConfiguratorBase {
	protected static final String REL_APPENDER = "appender";
	protected static final String METHOD_INIT_CUSTOM_APPENDER = "hookUpCustomAppender";
	protected String driverClassName;
	public AppenderConfiguratorBase(
			String zmBeanId, 
			String confNodeName,
			TrackerBase tracker,
			String driverClassName) {
		super(zmBeanId, confNodeName, tracker);
		this.driverClassName = driverClassName;
	}

	@Override
	protected void configureByContext(ConfigContext log4jConfCtx) {
		
		
		if(!autoHookUp)return;
		/*
		 * <appender class="org.zmonitor.logger.log4j.ZMonitorAppender">
		 *   the given class must extends org.zmonitor.logger.log4j.ZMonitorAppenderBase.
		 */
		final ConfigContext appenderCtx = log4jConfCtx.toNode(REL_APPENDER);
		Class<?> driverClz = null;
		try{
			driverClz = Class.forName(driverClassName);
			Driver driver = (Driver)driverClz.newInstance();
			driver.hookUpCustomAppender(new HookUpContext() {
				@Override
				public String getLogLevel() {
					return logLevel;
				}
				@Override
				public ConfigContext getAppenderCtx() {
					return appenderCtx;
				}
			});
		}catch(NoClassDefFoundError e){
			e.printStackTrace();
			ZMLog.info("cannot init Custom Appender for " +confNodeName+
					", application doesn't use this logger system.");
		} catch (ClassNotFoundException e) {
			throw new InitFailureException(e);
		} catch (SecurityException e) {
			throw new Error("this method must be public and accessable.", e);
		}catch (IllegalAccessException e) {
			ZMLog.warn(e);
		}  catch (InstantiationException e) {
			throw new Error("error while Driver construction: " +driverClz
					, e);
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
	 * If you want ZMonitor to add a custom appender to your application's log4j or Logback
	 * context while ZMonitor initialization. Configure this setting: <i>zmonitor.xml/log4j-conf:autoHookUp</i> 
	 * to true. This mechanism is designed for Developer who don't want to switch between 
	 * configurations of development environment and production environment.
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
	 * a specific container task intercepter (e.g. {@link org.zmonitor.web.ZMonitorFilter}), 
	 * and log4j (or logback) Factory is already started.
	 * @param autoHookUp
	 *            set to true to ask ZMonitor to insert a custom appender to
	 *            your application's log4j & logback context, default value is
	 *            <i>false</i>
	 */
	public void setAutoHookUp(boolean autoHookUp) {
		this.autoHookUp = autoHookUp;
	}
	
	private String logLevel = "ERROR";
	
	
	public String getLogLevel() {
		return logLevel;
	}
	/**
	 * Set the log level to the Hooked up appender, default   
	 * @param logLevel
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
	
}
