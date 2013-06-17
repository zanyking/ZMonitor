/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.MonitorPoint;
import org.zmonitor.selector.MPDefaultPseudoClassDefs;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.SelectorContext;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.slf4j.clz.BusinessObject;
import zmonitor.test.slf4j.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Slf4jMPSelector_TEST  extends TestBase{
	private static final Logger logger = 
			LoggerFactory.getLogger(Slf4jMPSelector_TEST.class);
	
	protected void runCase(){
		logger.info(">> start test case");
		new BusinessObject().doBiz();
		new Dao().getBean();
		logger.info("<< end test case");
	}
	
	@Test
	public void test_PseudoClass() throws Exception {
		testEntryIterator(".BusinessObject .Dao:greater-than(END, 50)", 1);
	}
	@Test
	public void test_PseudoClass2() throws Exception {
		testEntryIterator(".Dao.getBean:greater-than(END, 50)", 2);
	}
	
	/**
	 * 
	 * @param selector
	 * @param cond
	 * @throws Exception
	 */
	private void testEntryIterator(String selector, int cond) throws Exception{
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
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
