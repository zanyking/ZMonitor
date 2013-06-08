/**
 * 
 */
package zmonitor.test;

import java.util.Iterator;

import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitor;
import org.zmonitor.handler.SampleConsoleMonitorSequenceHandler;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.SelectionEntryBase;
import org.zmonitor.selector.Selectors;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.clz.A;
import zmonitor.test.clz.C;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleSelector_TEST  extends TestBase{

	@Test
	public void simpleTestSelectorEngine() throws Exception{
		
		ZMonitor.push("this is a test mesg!", true);
//		new C().doC1();
		new A().doA1();
		
		ZMonitor.pop(true);
		
		
		
		
		//====================================================================
		
		MonitoredResult result = this.getMonitoredResult();
		;
		SampleConsoleMonitorSequenceHandler handler = new SampleConsoleMonitorSequenceHandler();
		String selector = ".A .B .C";
		
		
		Iterator<Entry<MonitorPoint>> it = 
				Selectors.iterator(new MSWrapper(result.getAll().get(0)), 
						selector);
		Entry<MonitorPoint> entry;
		StringBuffer sb = new StringBuffer("Selector:\""+selector+"\" \n");
		
		while(it.hasNext()){
			entry = it.next();
			handler.writeMP(sb, entry.getValue(), " ");
			sb.append("--------------------\n");
		}
		System.out.println(sb);
	}
	
	
}
