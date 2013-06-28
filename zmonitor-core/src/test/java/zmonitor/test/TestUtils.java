/**
 * 
 */
package zmonitor.test;

import org.junit.Assert;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.EclipseConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MonitorPointSelection;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestUtils {

	public static void assertMPAmount(MonitorPointSelection mpSel, int expectedAmount){
		EclipseConsoleMonitorSequenceHandler handler = 
				ZMonitorManager.getInstance().getBeanIfAny(
						EclipseConsoleMonitorSequenceHandler.class);
		int counter = 0;
		MonitorPoint mp;
		StringBuffer sb = new StringBuffer("--------------------\n");
		while(mpSel.hasNext()){
			mp = mpSel.toNext();
			counter++;
			handler.writeMP(sb, mp, " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);
		Assert.assertEquals(expectedAmount, counter);
	}
}
