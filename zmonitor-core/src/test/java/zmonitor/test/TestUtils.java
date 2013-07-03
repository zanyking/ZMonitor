/**
 * 
 */
package zmonitor.test;

import org.junit.Assert;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.EclipseConsoleMonitorSequenceHandler;
import org.zmonitor.selector.MPDefaultPseudoClassDefs;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.SelectorContext;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.test.junit.MonitoredResult;

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
	
	
	public static void assertEntrySelector(String selector, int expected, MonitoredResult mResult){
		
		System.out.println("CURRENT SELECTOR is:"+selector);
		System.out.println("mResult size:"+mResult.size());
		MonitorSequence ms = mResult.get(0);
		
		EntryIterator<MonitorPoint> itor = 
				new EntryIterator<MonitorPoint>(new MSWrapper(ms), selector, 
						MPDefaultPseudoClassDefs.getDefaults());
		
		StringBuffer sb = new StringBuffer("testSelection_Selector: " +selector+
				"\n");
		sb.append("--------------------\n");
		
		int counter = 0;
		while(itor.hasNext()){
			counter++;
			itor.next();
			sb.append(itor.toString());
			sb.append("--------------------\n");
		}
		System.out.println(sb);		
		
		SelectorContext.printTree(itor.getRoot(), "   ");
		
		Assert.assertEquals(expected, counter);
	}
}
