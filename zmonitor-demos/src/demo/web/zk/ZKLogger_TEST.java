/**ZKLogger_TEST.java
 * 2011/11/8
 * 
 */
package demo.web.zk;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.zkoss.monitor.logger.java.ZMonitorHandler;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZKLogger_TEST {

	
	
	public static void main(String[] args){
		Logger rootLogger = Logger.getLogger("");//get RootLogger
		boolean hasZMonitor = false;
		for(Handler hl : rootLogger.getHandlers()){
			if(hl instanceof ZMonitorHandler){
				hasZMonitor = true;
				break;
			}
		}
		if(!hasZMonitor){
			rootLogger.addHandler(new ZMonitorHandler());
			
		}
	}
	
	
	private static void test(){
		Logger log = Logger.getLogger("test");//get RootLogger
//		log.
//		log.entering(sourceClass, sourceMethod, params);
	}
}
