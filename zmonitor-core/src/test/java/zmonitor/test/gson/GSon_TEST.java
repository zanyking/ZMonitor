/**
 * 
 */
package zmonitor.test.gson;

import org.junit.Test;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitor;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.TestUtils;
import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class GSon_TEST  extends TestBase{

	protected void runCase(){
		ZMonitor.push("this is a test mesg!", true);
		new Dao().getBean();
		new BusinessObject().doBiz();
		ZMonitor.pop(true);	
	}
	@Test
	public void monitorSequence2JSon(){ 
		MonitoredResult result = this.getMonitoredResult();
//		MonitorPointSelection mpSel = result.asSelection().select(".Dao.getBean");
//		TestUtils.assertMPAmount(mpSel, 4);
		MonitorSequence ms = result.get(0);
		
	}
}
