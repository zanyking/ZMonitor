/**
 * 
 */
package zmonitor.test;

import org.junit.Test;
import org.zmonitor.ZMonitor;

/**
 * @author ian
 *
 */
public class ZmonitorNativeAPI_TEST extends TestBase {

	@Test
	public void simpleTestWithSubRoutine() throws Exception{
		ZMonitor.push("this is a test mesg!", true);
		{
			ZMonitor.push("second stack", true);
			ZMonitor.pop(true);	
		}
		ZMonitor.record("");
		
		ZMonitor.push("> method 1", true);
		method1();
		ZMonitor.pop("< method 1", true);	
		
		ZMonitor.pop(true);
	}
	
	private static void method1() throws Exception{
		ZMonitor.push("start method 1", true);
		ZMonitor.record("point 1");
		Thread.sleep(1234);
		ZMonitor.record("point 2");
		ZMonitor.pop(true);
		
		
		ZMonitor.push("new Timeline", true);
		ZMonitor.pop(true);
	}
}
