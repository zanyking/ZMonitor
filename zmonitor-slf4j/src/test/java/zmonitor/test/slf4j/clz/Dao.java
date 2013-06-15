/**
 * 
 */
package zmonitor.test.slf4j.clz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Dao {
	private static final Logger logger = 
			LoggerFactory.getLogger(Dao.class);
	
	/**
	 * 
	 */
	public Dao() {
		logger.trace(" Dao constructed.");
	}

	public void getBean() {
		logger.trace(">> getBean() user:{}, ID:{}", "Ian Tsai", 12);
		lookUpDB();
		logger.trace("<< getBean()");
	}

	private void lookUpDB(){
		logger.trace(">> lookUpDB() hello world!");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.warn("InterruptedException occurred: "+e);
		}
		finally{
			logger.trace("<< lookUpDB()");	
		}
		
	}
}
