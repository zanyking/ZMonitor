/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.EclipseConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;
import org.zmonitor.util.Strings;

import zmonitor.test.slf4j.clz.BusinessObject;
import zmonitor.test.slf4j.clz.Dao;


/**
 * @author Ian YT Tsai(Zanyking)
 * 
 */
public class Slf4jSimple_TEST extends TestBase{
	private static final Logger logger = 
			LoggerFactory.getLogger(Slf4jSimple_TEST.class);
	
	private void runCase(){
		logger.info(">> start test case");
		new BusinessObject().doBiz();
		new Dao().getBean();
		logger.info("<< end test case");
		
	}
	
	@Test
	public void test_Configuration() throws Exception {
		runCase();
		// 1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();

		// 2. Use Selection API to manipulate the Monitor Point Sequence.
		String selector = ".BusinessObject .Service .Dao .lookUpDB[message*='ppp']:greater-than(END,50)";

		MonitorPointSelection mpSel = mResult.asSelection().select(selector);

		MonitorPoint mp;
		StringBuffer sb = new StringBuffer(
				"testSelection_SelectorPseudoClass: " + selector + "\n");
		sb.append("--------------------\n");
		EclipseConsoleMonitorSequenceHandler handler = ZMonitorManager
				.getInstance().getBeanById("console-handler");

		int counter = 0;
		while (mpSel.hasNext()) {
			counter++;
			mp = mpSel.toNext();
			Strings.appendln(sb, " Start printing from root:");
			handler.writeRoot2MP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);
	}

}
