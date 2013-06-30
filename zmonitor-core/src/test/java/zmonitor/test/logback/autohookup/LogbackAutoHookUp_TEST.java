/**
 * 
 */
package zmonitor.test.logback.autohookup;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.test.junit.LogbackTestBase;

import zmonitor.test.slf4j.clz.BusinessObject;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LogbackAutoHookUp_TEST extends LogbackTestBase{

	private static final Logger logger = LoggerFactory.getLogger(LogbackAutoHookUp_TEST.class);
	@Test
	public void simpleTest(){
		logger.debug(">> Helloworld! name:{}, age:{}, married:{}", 
				"Ian Tsai", 32, false);
			new BusinessObject().doBiz();
		logger.debug("<< Finish");
	}
}
