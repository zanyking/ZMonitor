/** 2013/04/24
 * 
 */
package org.zmonitor.slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.zmonitor.MonitorPoint;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.slf4j.marker.AdaptiveMarkerFactory;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.PropertySetterException;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class Slf4jConfigurator extends ZMBeanBase implements Configurator{

	private static final String REL_SLF4J_CONF = "slf4j-conf";
	private static final String REL_PUSH = "push"; 
	private static final String REL_POP = "pop";
	
	public void configure(ConfigContext monitorMgmt) {
		ZMonitorManager manager = monitorMgmt.getManager();
		manager.setMarkerFactory(new AdaptiveMarkerFactory());
		manager.getSelectorAdaptor().addSupport(Markers.TRACKER_NAME_SLF4J, 
				new DefaultSelectorAdaptation() {
			public String retrieveType(MonitorPoint mp) {
				return Markers.retrieveRegisteredMajorMarker(mp.getMonitorMeta()).getName();
			}
		});
		
		ConfigContext slf4jConfCtx = monitorMgmt.toNode(REL_SLF4J_CONF);
		if(slf4jConfCtx.getNode()!=null){
			 preparSelf(slf4jConfCtx);
		}
	}
	
	private void preparSelf(ConfigContext slf4jConfCtx){
		PropertySetter slf4jConfSetter = new PropertySetter(this);
		slf4jConfCtx.applyAttributes(slf4jConfSetter);
		preparePushOp(slf4jConfCtx.toNode(REL_PUSH));
		preparePopOp(slf4jConfCtx.toNode(REL_POP));
		//logger settings:
		preparLoggerProperties(slf4jConfCtx);
	}
	private void preparLoggerProperties(ConfigContext slf4jConfCtx){
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
	
	private void preparePushOp(ConfigContext pushOpCtx){
		String op = pushOpCtx.getContent();
		if(Strings.isEmpty(op))pushOp = op;
	}
	
	private void preparePopOp(ConfigContext popOpCtx) {
		String op = popOpCtx.getContent();
		if(Strings.isEmpty(op))popOp = op;
	}
	private Map<String, LogLevel> loggerProperties = 
			new LinkedHashMap<String, LogLevel>();
	
	private LogLevel defaultLogLevel = LogLevel.TRACE;
	private String pushOp = ">>";
	private String popOp = "<<";
	
	private boolean eatOperator = true;
	
	public boolean isEatOperator() {
		return eatOperator;
	}
	public void setEatOperator(boolean eatOperator) {
		this.eatOperator = eatOperator;
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
	
	public void tracking(TrackingContext tCtx) {
		MessageTuple mt = (MessageTuple) tCtx.getMessage();
		String mesg = mt.getMessagePattern();
		
		if(mesg==null){	//No info to identify what's going on, should not 
						//happened because a message of Logger.log(String message) 
						//should never be null. 
			ZMonitor.record(tCtx);
		}else{
			if(mesg.startsWith(pushOp)){
				if(eatOperator){
					mt.setMessagePattern(mesg.substring(pushOp.length()));
				}
				ZMonitor.push(tCtx);
			}else if(mesg.startsWith(popOp)){
				if(eatOperator){
					mt.setMessagePattern(mesg.substring(popOp.length()));
				}
				ZMonitor.pop(tCtx);	
			}else{
				ZMonitor.record(tCtx);
			}
		}
	}

	
	
	
}
