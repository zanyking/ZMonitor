/** 2013/04/24
 * 
 */
package org.zmonitor.slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.logger.ConfiguratorBase;
import org.zmonitor.logger.TrackerBase;
import org.zmonitor.slf4j.marker.AdaptiveMarkerFactory;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.PropertySetterException;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class Slf4jConfigurator extends ConfiguratorBase{
	private static final String REL_SLF4J_CONF = "slf4j-conf";
	
	private Map<String, LogLevel> loggerProperties = 
			new LinkedHashMap<String, LogLevel>();
	
	private LogLevel defaultLogLevel = LogLevel.TRACE;
	
	public Slf4jConfigurator() {
		super(REL_SLF4J_CONF, REL_SLF4J_CONF, new Slf4jTracker());
	}
	
	
	@Override
	protected void configureDefault(ZMonitorManager manager) {
		manager.setMarkerFactory(new AdaptiveMarkerFactory());
		manager.getSelectorAdaptor().addSupport(Markers.TRACKER_NAME_SLF4J, 
				new DefaultSelectorAdaptation() {
			public String retrieveType(MonitorPoint mp) {
				return Markers.retrieveRegisteredMajorMarker(mp.getMonitorMeta()).getName();
			}

			@Override
			public Object resolveAttribute(String varName, MonitorPoint mp) {
				return new Slf4jMPVariableResolver(mp).resolveVariable(varName);
			}
		});	
	}

	@Override
	protected void configureByContext(ConfigContext slf4jConfCtx) {
		PropertySetter propsSetter = 
				new PropertySetter(loggerProperties, new PropertySetter.Interceptor() {
					public Object intercept(String name, String value)
							throws PropertySetterException {
						LogLevel lgLv = LogLevel.valueOf(value.toUpperCase());
						if(lgLv==null){
							throw new PropertySetterException(Strings.append("value of: [/",
									REL_SLF4J_CONF,"/",ConfigContext.PROPERTY,"/] ",
									ConfigContext.NAME,"=",name,
									" is not a valid log level: value=",value));
						}
						return lgLv;
					}
				});
		slf4jConfCtx.applyPropertyTags(propsSetter);	
	}
	
	/**
	 * 
	 * @param name 
	 * @return
	 */
	public LogLevel getLogLevel(String name){
		
		String tempName = name;
		LogLevel logLv = null;
		int indexOfLastDot = tempName.length();
		while ((logLv == null) && (indexOfLastDot > -1)) {
			tempName = tempName.substring(0, indexOfLastDot);
			logLv = loggerProperties.get(tempName);
			indexOfLastDot = String.valueOf(tempName).lastIndexOf(".");
		}
		if(logLv==null)
			return defaultLogLevel;
		else
			return logLv;
	}
	
	public String getDefaultLogLevel(){
		return defaultLogLevel.name();
	}
	/**
	 * 
	 * @param logLevel
	 */
	public void setDefaultLogLevel(String logLevel){
		this.defaultLogLevel = LogLevel.valueOf(logLevel.toUpperCase());
	}
	/**
	 * 
	 * @param logLevelCode
	 */
	public void setDefaultLogLevel(int logLevelCode){
		this.defaultLogLevel = LogLevel.toLogLevel(logLevelCode);
	}

	

	
	
	
}
