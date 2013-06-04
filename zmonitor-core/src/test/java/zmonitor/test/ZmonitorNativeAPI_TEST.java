/**
 * 
 */
package zmonitor.test;

import java.util.List;

import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.selector.MonitorPointSelectionBase;
import org.zmonitor.selector.Selection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.Iterators;
import org.zmonitor.util.RangeRetrievers;

/**
 * @author ian
 *
 */
public class ZmonitorNativeAPI_TEST extends TestBase {

	public ZmonitorNativeAPI_TEST() {
		super(true);
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
//		List<MonitorSequence> list = result.getAll();
		
		//2. use Selection
		
		
		MonitorPointSelectionBase mpSel = 
			(MonitorPointSelectionBase) result.asSelection()
//		.select("") //Use JQuery Selector 
		.greaterThan(RangeRetrievers.END, 200L)
		;
		
		
//		List<MonitorPoint> mps = mpSel.toList();
//		System.out.println("Iterators.toString(mpSel): "+
//				Iterators.toString(mpSel));
		
		if (!mpSel.hasNext()) {
			System.out.println("mpSel is empty!");
			return;
		}
		Entry<MonitorPoint> mp = mpSel.next();
		System.out.println("first mp is : "+mp);
		
		while (mpSel.hasNext()) {
			System.out.println("mp is : "+mpSel.next());
		}
		
	}
	
	
}
