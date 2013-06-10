/** 2013/04/24
 * 
 */
package org.zmonitor.slf4j;

import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.slf4j.marker.AdaptiveMarkerFactory;
import org.zmonitor.spi.Configurator;

/**
 * @author Ian YT Tsai(Zanyking)
 */
public class Slf4jConfigurator extends ZMBeanBase implements Configurator{

//	private String 
	
	public void configure(ConfigContext configCtx) {
		configCtx.getManager().setMarkerFactory(
				new AdaptiveMarkerFactory());
		
	}
	
	private LogLevel logLevel = LogLevel.TRACE;
	private String pushOp = ">>";
	private String popOp = "<<";
	
	private boolean eatOperator = true;
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
