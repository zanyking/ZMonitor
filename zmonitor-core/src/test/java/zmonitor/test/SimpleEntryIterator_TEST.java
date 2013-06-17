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
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.SelectorContext;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleEntryIterator_TEST  extends TestBase{
	
	protected void runCase(){
		ZMonitor.push("this is a test mesg!", true);
		new Dao().getBean();
		new BusinessObject().doBiz();
		ZMonitor.pop(true);	
	}
	
	@Test
	public void test_CB_DECENDENT() throws Exception{
		testSelection_Selector(".BusinessObject .Dao.getBean[message*='ppp']", 1);
	}
	@Test
	public void test_CB_DECENDENT2() throws Exception{
		testSelection_Selector(" .Service .Dao .getBean", 1);// it's one!
	}
	@Test
	public void test_JOINED_CLASSES() throws Exception{
		testSelection_Selector(" .Dao.getBean", 4);
	}
	@Test
	public void test_JOINED_CLASSES2() throws Exception{
		testSelection_Selector(" .Dao._init_", 2);
	}
	@Test
	public void test_CB_ADJACENT_SIBLING() throws Exception{
		testSelection_Selector(".BusinessObject ~ .doBiz", 1);
	}
	@Test
	public void test_CB_CHILD() throws Exception{
		testSelection_Selector(".runCase >  .BusinessObject.doBiz", 1);
	}
	
	private void testSelection_Selector(String selector, int cond) throws Exception{
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		
		//2. Use Selection API to manipulate the Monitor Point Sequence. 
		EntryIterator<MonitorPoint> itor = 
				new EntryIterator<MonitorPoint>(new MSWrapper(mResult.get(0)), selector, null);

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
