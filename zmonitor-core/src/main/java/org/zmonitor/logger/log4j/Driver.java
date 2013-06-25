/**
 * 
 */
package org.zmonitor.logger.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Driver {
	
	/**
	 * 
	 * @param appenderCtx
	 */
	public static void hookUpCustomAppender(ConfigContext appenderCtx){
		Logger root = Logger.getRootLogger();
		ZMonitorAppenderBase log4jAppender;
		if(appenderCtx.getNode()==null){
			//use default setting... 
			log4jAppender = new ZMonitorAppender();
		}else{
			//use custom setting...
			// must extends ZMonitorAppenderBase...
			log4jAppender = appenderCtx.newBean(
					ZMonitorAppender.class, false);
			appenderCtx.applyAttributes(new PropertySetter(log4jAppender), 
					"class");
		}
		log4jAppender.setThreshold(Level.DEBUG);
		root.addAppender(log4jAppender);
	}
	
	
}
