/**
 * 
 */
package zmonitor.test.web;

import org.junit.Test;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.webtest.WebTestBase;
import org.zmonitor.webtest.WebTestResponse;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleGet_TEST extends WebTestBase {
	
	@Test
	public void simpleTest() throws Exception{
		WebTestResponse webResp = super.doGet("http://localhost:8080/test-web/demo");
		//HttpUnit
		
		//JSR JAXRS...

//		logger.info("{}{}{}",1, false, "Ian Tsai");
//		TestUtils.assertMPAmount(result.asSelection().select(".Dao.getBean[message.0=1]"), 1);
		
		MonitoredResult result = webResp.getMonitoredResult();
		TestUtils.assertMPAmount(result.asSelection().select(".Dao.getBean"), 2);
		TestUtils.assertMPAmount(result.asSelection().select("[message*='Chopin']"), 1);
	}

}
