/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zmonitor.test.TestBase;

/**
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class Slf4jSimple_TEST extends TestBase{

	@Test
	public void simpleUsage() throws Exception {
		Logger logger = LoggerFactory.getLogger(Slf4jSimple_TEST.class);
		logger.info("Hello World");
		
	}

}
