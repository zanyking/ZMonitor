/** 2013/04/24
 * 
 */
package org.zmonitor.slf4j;

import org.slf4j.spi.LocationAwareLogger;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.Configurator;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class Slf4jConfigurator extends ZMBeanBase implements Configurator{

//	private String 
	
	public void configure(ConfigContext configCtx) {
		
	}
	
	private LogLevel logLevel;
	/**
	 * 
	 * @return
	 */
	public LogLevel getLogLevel(){
		return logLevel;
	}
	/**
	 * 
	 * @param logLevel
	 */
	public void setLogLevel(String logLevel){
		this.logLevel = LogLevel.valueOf(logLevel);
	}
	/**
	 * 
	 * @param logLevelCode
	 */
	public void setLogLevel(int logLevelCode){
		this.logLevel = LogLevel.toLogLevel(logLevelCode);
	}

}
