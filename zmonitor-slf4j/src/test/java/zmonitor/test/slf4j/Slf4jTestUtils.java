/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Assert;
import org.zmonitor.MonitorPoint;
import org.zmonitor.selector.MPDefaultPseudoClassDefs;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.SelectorContext;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.test.junit.MonitoredResult;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Slf4jTestUtils {
	/**
	 * 
	 * @param selector
	 * @param cond
	 * @param mResult
	 * @throws Exception
	 */
	public static void testEntryIterator(String selector, int cond, MonitoredResult mResult) throws Exception{
		
		//1. Get the monitored result through ZMonitor TestBase API.
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		EntryIterator<MonitorPoint> itor = 
				new EntryIterator<MonitorPoint>(
						new MSWrapper(mResult.get(0)), 
						selector, 
						MPDefaultPseudoClassDefs.getDefaults());

		StringBuffer sb = new StringBuffer(
				"testSelection_Selector: " +selector+"\n");
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
		
		Assert.assertEquals(cond, counter);
	}
}
