/**
 * 
 */
package zmonitor.test.log4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.test.junit.Log4JTestBase;

import zmonitor.test.TestUtils;
import zmonitor.test.clz.node.A;
import zmonitor.test.clz.node.D;
import zmonitor.test.clz.node.E;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
@RunWith(Parameterized.class)
public class Log4jMonitor_TEST extends Log4JTestBase{

	
	static{
		EntryIterator.IS_DEBUG = false;
	}
	private static final Logger logger = Logger.getLogger(Log4jMonitor_TEST.class);
	/*
	 * A ROOT
	 * |-D  
	 *   |- E 
	 *   |- D
	 *   |- E
	 *   |- D
	 *   |- D END
	 * |- A END
	 * 
	 */
	protected void runCase() {
		logger.debug(">> start testing...");
		A root = new A();
		root.toFirstChild(D.D_FAC.newFac(
				">> do list classical music artist", 
				"<< end of list"))
				.toFirstChild(E.E_FAC.newFac(" Frederic Chopin", null))
				.toNextSibling(D.D_FAC.newFac(" Pyotr Ilyich Tchaikovsky", null))
				.toNextSibling(E.E_FAC.newFac(" Wolfgang Amadeus Mozart", null))
				.toNextSibling(D.D_FAC.newFac(" Robert Alexander Schumann", null));
		root.doNode();
		logger.debug("<< End case");
	}

	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[]{".D > [message*='Chopin']", 1});
		params.add(new Object[]{".A  [message*='Chopin']", 1});
		params.add(new Object[]{".E + .D", 2});
		return params;
	}
	private String selector; 
	private int expectedCount;
	
	public Log4jMonitor_TEST(String selector, int expectedCount) {
		this.selector = selector;
		this.expectedCount = expectedCount;
	}

	@Test
	public void simpleLog4jNode(){
		MonitorPointSelection mpSel = this.getMonitoredResult()
				.asSelection().select(selector);
		System.out.println("\n-----------------------");
		
		TestUtils.assertMPAmount(mpSel, expectedCount);
	}
	
	
}
