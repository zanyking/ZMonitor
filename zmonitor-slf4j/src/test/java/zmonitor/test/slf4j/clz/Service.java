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
		logger.trace(">> constructing Service...", true);
		this.c = c;
		logger.trace("<< Service constructed.", true);
	}



	public void doService() {
		logger.trace(">> doService()", true);
		c.getBean();
		logger.trace("<< doService()", true);
	}

}
