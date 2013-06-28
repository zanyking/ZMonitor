/**
 * 
 */
package zmonitor.test.logback;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.test.junit.LogbackTestBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleLogback_TEST extends LogbackTestBase{

	private static final Logger logger = LoggerFactory.getLogger(SimpleLogback_TEST.class);
	@Test
	public void simpleTest(){
		logger.debug(">> Helloworld!");
			
		logger.debug("<< Finish");
	}
}
