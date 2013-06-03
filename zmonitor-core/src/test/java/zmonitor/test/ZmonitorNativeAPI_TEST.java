/**
 * 
 */
package zmonitor.test;

import java.util.List;

import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.selector.Selection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

/**
 * @author ian
 *
 */
public class ZmonitorNativeAPI_TEST extends TestBase {

	public ZmonitorNativeAPI_TEST() {
		super(true);
	}

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
		
		MonitoredResult result = this.getMonitoredResult();

		//TWO approaches...
		
		//1. get all monitored sequence, and handled it by yourself
		List<MonitorSequence> list = result.getAll();
		
		//2. use Selection
		
		
		Selection<MonitorPoint> selection = result.asSelection()
			.select("") //Use JQuery Selector 
			.filter(ellipse(Period.END, Operation.GREATER, 1000));
		
		List<MonitorPoint> mps = selection.toList();
		
//			.eq("attr1", 1, 123);
		
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
