package zmonitor.test.slf4j.clz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BusinessObject {
	private static final Logger logger = 
			LoggerFactory.getLogger(BusinessObject.class);
	
	Service b;
	
	public BusinessObject() {
		logger.trace(">> constructing BusinessObject...");
		b = new Service(new Dao());
		logger.trace("<< BusinessObject constructed.");
	}

	
	public void doBiz(){
		logger.trace(">> doBiz() hello world!");
		b.doService();
		logger.trace("<< doBiz()");
	}
}
