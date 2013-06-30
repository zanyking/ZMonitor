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
public class Service {

	private static final Logger logger = 
			LoggerFactory.getLogger(Service.class);
	
	
	Dao c;
	
	public Service(Dao c) {
		logger.trace(">> constructing Service... hello world!");
		this.c = c;
		logger.trace("<< Service constructed.");
	}



	public void doService() {
		logger.trace(">> doService()");
		c.getBean();
		logger.trace("<< doService()");
	}
	
	public void doService2() {
		logger.trace(">> doService2()");
		c.getBean();
		logger.trace("<< doService2()");
	}

}
