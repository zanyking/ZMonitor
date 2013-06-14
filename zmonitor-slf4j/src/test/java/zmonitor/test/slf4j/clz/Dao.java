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
		logger.trace(" Dao constructed.", true);
	}

	public void getBean() {
		logger.trace(">> getBean() hello world!", true);
		lookUpDB();
		logger.trace("<< getBean()", true);
	}

	private void lookUpDB(){
		logger.trace(">> lookUpDB() hello world!", true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			logger.warn("InterruptedException occurred: "+e, true);
		}
		finally{
			logger.trace("<< lookUpDB()", true);	
		}
		
	}
}
