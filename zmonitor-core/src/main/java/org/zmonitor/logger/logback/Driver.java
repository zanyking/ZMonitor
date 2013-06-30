/**
 * 
 */
package org.zmonitor.logger.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.config.ConfigContext;

import ch.qos.logback.classic.LoggerContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Driver implements org.zmonitor.logger.Driver{

	
	
	public void hookUpCustomAppender(ConfigContext appenderCtx){
		LoggerContext logCtx = (LoggerContext)LoggerFactory.getILoggerFactory();
		ZMonitorAppender zmAppender = new ZMonitorAppender();
		zmAppender.setContext(logCtx);
		zmAppender.start();
		logCtx.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(zmAppender);
	}
}
