/**
 * 
 */
package org.zmonitor.logger.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.util.PropertySetter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Driver implements org.zmonitor.logger.Driver{

	
	
	public void hookUpCustomAppender(HookUpContext hookupCtx){
		ConfigContext appenderCtx = hookupCtx.getAppenderCtx();
		LoggerContext logCtx = (LoggerContext)LoggerFactory.getILoggerFactory();
		ZMonitorAppender logbackAppender;
		if(appenderCtx.getNode()==null){
			//use default setting... 
			logbackAppender = new ZMonitorAppender();
		}else{
			//use custom setting...
			// must extends ZMonitorAppenderBase...
			logbackAppender = appenderCtx.newBean(
					ZMonitorAppender.class, false);
			
			appenderCtx.applyAttributes(new PropertySetter(logbackAppender), 
					"class");
		}
		
		
		logbackAppender.setContext(logCtx);
		logbackAppender.start();
		ch.qos.logback.classic.Logger rootLogger = logCtx.getLogger(Logger.ROOT_LOGGER_NAME);
		Level lv = Level.toLevel(hookupCtx.getLogLevel());
		rootLogger.setLevel(lv);
		rootLogger.addAppender(logbackAppender);
	}

	
	
}
