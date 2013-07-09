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
	public void simpleTest(){
		WebTestResponse webResp = super.doGet("http://localhost:8080/test-web/", null);
		MonitoredResult result = webResp.getMonitoredResult();
		result.asSelection().select("");
	}

}
