/**
 * 
 */
package zmonitor.test;

import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.handler.SampleConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.RangeRetriever;

import zmonitor.test.clz.BusinessObject;

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

		try{
			
			ZMonitor.record("point 1");
			ZMonitor.record("point 2");
			Thread.sleep(1234);	
			new BusinessObject().doBiz();
			
		}catch(Exception e){
			ZMonitor.record("exception: "+e.getMessage());// logger.error(); logger.debug();
		}
		
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
		
		String selector = "push";
		 MonitorPointSelection mpSel = result.asSelection()
			.select(selector) //Use JQuery Selector
			.greaterThan(RangeRetriever.Default.END, 200L)		 
			;
		 
//		 boolean anyPushMpWithException = mpSel.any(new Predicate<MonitorPoint>() {
//				public boolean apply(MonitorPoint mp) {
//					//TODO see if any message starts with "hello";
//					return mp.getMessage().toString().indexOf("exception") >= 0;
//				}
//			});
		
		
		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("Selector:\""+selector+"\" \n");
		SampleConsoleMonitorSequenceHandler handler = new SampleConsoleMonitorSequenceHandler();
		while(mpSel.hasNext()){
			mp = mpSel.toNext();
			handler.writeMP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		
	}
	
	
}
