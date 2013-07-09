/**
 * 
 */
package zmonitor.test.web;

import org.junit.Test;
import org.zmonitor.webtest.WebTestBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleGet_TEST extends WebTestBase {
	
	@Test
	public void simpleTest(){
		super.doGet("http://localhost:8080/test-web/", null);
	}

}
