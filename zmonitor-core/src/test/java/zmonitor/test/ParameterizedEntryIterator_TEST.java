/**
 * 
 */
package zmonitor.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zmonitor.ZMonitor;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.clz.BusinessObject;
import zmonitor.test.clz.Dao;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
@RunWith(Parameterized.class)
public class ParameterizedEntryIterator_TEST  extends TestBase {

	protected void runCase(){
		ZMonitor.push("this is a test mesg!", true);
		new Dao().getBean();
		new BusinessObject().doBiz();
		ZMonitor.pop(true);	
	}
	
	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[]{".runCase > .Dao ~ .BusinessObject", 2});
		params.add(new Object[]{".runCase  .Dao ~ .BusinessObject", 3});
		params.add(new Object[]{".BusinessObject .Service .Dao[message*='ppp']", 2});
		params.add(new Object[]{".BusinessObject .Dao[message*='ppp']", 2});
		params.add(new Object[]{".BusinessObject .Service .Dao.lookUpDB[message*='ppp']:greater-than(END,50)", 1});
		return params;
	}
	
	private String selector;
	private int selected = 0;
	
	public ParameterizedEntryIterator_TEST(String selector, int selected){
		this.selector = selector;
		this.selected = selected;
	}

	@Test
	public void test_EntrySelector() throws Exception{
		
		//1. Get the monitored result through ZMonitor TestBase API.
		MonitoredResult mResult = this.getMonitoredResult();
		TestUtils.assertEntrySelector(selector, selected, mResult);
	}

	
	

}
