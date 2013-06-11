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
import org.zmonitor.util.RangeRetriever;
import org.zmonitor.util.Strings;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleSelector_TEST  extends TestBase{

	private void runCase() {
		ZMonitor.push("this is a test mesg!", true);
		new BusinessObject().doBiz();
		new Dao().getBean();
		ZMonitor.pop(true);		
	}
	@Test
	public void testSelection_Selector() throws Exception{
		runCase();
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		String selector = ".BusinessObject .Dao[message*='ppp']";
		MonitorPointSelection mpSel = mResult.asSelection()
				.select(selector)
				.greaterThan(RangeRetriever.Default.END, 50L);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("testSelection_Selector: " +selector+
				"\n");
		sb.append("--------------------\n");
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
	public void testSelection_SelectorPseudoClass() throws Exception{
		runCase();
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		String selector = 
			".BusinessObject .Service .Dao .lookUpDB[message*='ppp']:greater-than(END,50)";
		
		MonitorPointSelection mpSel = mResult.asSelection()
				.select(selector);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("testSelection_SelectorPseudoClass: " +selector+
				"\n");
		sb.append("--------------------\n");
		EclipseConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanById("console-handler");
		
		int counter = 0;
		while(mpSel.hasNext()){
			counter++;
			mp = mpSel.toNext();
			Strings.appendln(sb, " Start printing from root:");
			handler.writeRoot2MP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		Assert.assertEquals(1, counter);
	}
	
}
