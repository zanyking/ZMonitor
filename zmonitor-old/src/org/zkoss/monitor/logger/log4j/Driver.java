/**Log4jZMonitorIgniter.java
 * 2011/10/21
 * 
 */
package org.zkoss.monitor.logger.log4j;

import org.apache.log4j.Logger;
import org.zkoss.monitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Driver {
	
	static{
		try{
//			System.out.println(">>>>>> org.zkoss.monitor.log4j.Driver");
			Logger.getLogger(ZMonitor.class);//trigger default config...
			
			if(ZMonitorAppender.getInstance()==null){
				//ZMonitor is ignited by other way
				//there might has no log4j configuration or has no ZMonitorAppender in log4j.xml 
				
				Logger root = Logger.getRootLogger();
				root.addAppender(new ZMonitorAppender());
				
//				System.out.println(">>>>> RootLogger add appender: "+ZMonitorAppender.class.getSimpleName());
					
			}
		}catch(Throwable e){
			throw new RuntimeException(e);
		}
		
	}

}
