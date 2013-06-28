/**
 * 
 */
package zmonitor.test;

import org.junit.Assert;
import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.EclipseConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.Predicate;
import org.zmonitor.util.RangeRetriever;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimplSelection_TEST extends TestBase {

	
	
	protected void runCase(){
		ZMonitor.push("this is a test mesg!", true);
			new BusinessObject().doBiz();
			new Dao().getBean();
		ZMonitor.pop(true);
		
	}

	Predicate<MonitorPoint> letByMessageContainsPPP = new Predicate<MonitorPoint>() {
		public boolean apply(MonitorPoint mp) {
			// find out the mp which message contains "ppp"
			if(mp.getMessage()==null)return false;
			return mp.getMessage().toString() .indexOf("ppp") >= 0;
		}
	};
	
	
	@Test
	public void testSelection_Traverse() throws Exception{
	
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		
		MonitorPointSelection mpSel = mResult.asSelection().traverse(
			letByMessageContainsPPP, Predicate.TRUE);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("traverse with letBy\n");
		EclipseConsoleMonitorSequenceHandler handler = 
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
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		MonitorPointSelection mpSel = mResult.asSelection()
				.traverse(letByMessageContainsPPP, Predicate.TRUE)
				.greaterThan(RangeRetriever.Default.END, 50L);
		
		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("traverse with letBy | greaterThan \n");
		
		EclipseConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanById("console-handler");
		
		int counter = 0;
		while(mpSel.hasNext()){
			counter++;
			mp = mpSel.toNext();
			handler.writeRoot2MP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		Assert.assertEquals(4, counter);
	}
	
	
	
}
