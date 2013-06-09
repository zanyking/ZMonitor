/**
 * 
 */
package zmonitor.test;

import org.junit.Assert;
import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.SampleConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.Predicate;
import org.zmonitor.util.RangeRetrievers;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimplSelection_TEST extends TestBase {

	
	
	
	
	private void runCase(){
		
		ZMonitor.push("this is a test mesg!", true);
			new BusinessObject().doBiz();
			new Dao().getBean();
		ZMonitor.pop(true);
		
	}

	Predicate<MonitorPoint> letByMessageContainsPPP = new Predicate<MonitorPoint>() {
		public boolean apply(MonitorPoint mp) {
			// find out the mp which message contains "ppp"
			return mp.getMessage().toString() .indexOf("ppp") >= 0;
		}
	};
	
	
	@Test
	public void testSelection_Traverse() throws Exception{
	
		runCase();
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		
		MonitorPointSelection mpSel = mResult.asSelection().traverse(
			letByMessageContainsPPP, Predicate.TRUE);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("traverse with letBy\n");
		SampleConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanById("console-handler");
		int counter = 0;
		while(mpSel.hasNext()){
			counter++;
			mp = mpSel.toNext();
			handler.writeMP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		
		Assert.assertEquals(4, counter);
	}
	
	
	@Test
	public void testSelection_Traverse$GreaterThan() throws Exception{
		runCase();
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		MonitorPointSelection mpSel = mResult.asSelection()
				.traverse(letByMessageContainsPPP, Predicate.TRUE)
				.greaterThan(RangeRetrievers.END, 50L);
		
		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("traverse with letBy | greaterThan \n");
		
		SampleConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanById("console-handler");
		
		int counter = 0;
		while(mpSel.hasNext()){
			counter++;
			mp = mpSel.toNext();
			handler.writeMP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		Assert.assertEquals(2, counter);
	}
	
	@Test
	public void testSelection_Selector() throws Exception{
		runCase();
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		String selector = ".BusinessObject .Service .Dao[message*='ppp']";
		MonitorPointSelection mpSel = mResult.asSelection()
				.select(selector)
				//.traverse(letByMessageContainsPPP, Predicate.TRUE)
//				.filter(letByMessageContainsPPP)
				.greaterThan(RangeRetrievers.END, 50L);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("testSelection_Selector: " +selector+
				"\n");
		sb.append("--------------------\n");
		SampleConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanById("console-handler");
		
		int counter = 0;
		while(mpSel.hasNext()){
			counter++;
			mp = mpSel.toNext();
			handler.writeMP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		Assert.assertEquals(1, counter);
	}
	
}
