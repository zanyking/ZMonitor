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
import org.zmonitor.test.junit.Log4JTestBase;
import org.zmonitor.test.junit.MonitoredResult;

import zmonitor.test.TestUtils;
import zmonitor.test.log4j.clz.node.A;
import zmonitor.test.log4j.clz.node.D;
import zmonitor.test.log4j.clz.node.E;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
@RunWith(Parameterized.class)
public class NodeParameterizedEntryIterator_TEST  extends Log4JTestBase{
	private static final Logger logger = Logger.getLogger(NodeParameterizedEntryIterator_TEST.class);
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
	public void runCase() {
		logger.debug(">> start testing...");
		A root = new A();
		root.toFirstChild(D.D_FAC.newFac(
				">> do list classical music artist", 
				"<< end of list"))
				.toFirstChild(D.D_FAC.newFac(" Frederic Chopin", null))
				.toNextSibling(E.E_FAC.newFac(" Frederic Chopin", null))
				.toNextSibling(D.D_FAC.newFac(" Pyotr Ilyich Tchaikovsky", null))
				.toNextSibling(D.D_FAC.newFac(" John Sebastian Bach", null))
				.toNextSibling(E.E_FAC.newFac(" Wolfgang Amadeus Mozart", null))
				.toNextSibling(D.D_FAC.newFac(" Robert Alexander Schumann", null));
		root.doNode();
		logger.debug("<< End case");
	}
	@Parameters
	public static Collection<Object[]> data(){
		List<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[]{".D > [message*='Chopin']", 2});
		params.add(new Object[]{".A  [message*='Chopin']", 2});
		params.add(new Object[]{".E + .D", 2});
		return params;
	}
	private String selector; 
	private int expectedCount;
	
	public NodeParameterizedEntryIterator_TEST(String selector, int expectedCount) {
		this.selector = selector;
		this.expectedCount = expectedCount;
	}

	@Test
	public void testSelectorEntryItor(){
		MonitoredResult mResult = this.getMonitoredResult();
		TestUtils.assertEntrySelector(selector, expectedCount, mResult);
	}
}
