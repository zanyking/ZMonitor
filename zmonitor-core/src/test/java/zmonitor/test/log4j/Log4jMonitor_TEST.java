/**
 * 
 */
package zmonitor.test.log4j;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.EclipseConsoleMonitorSequenceHandler;
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
public class Log4jMonitor_TEST extends Log4JTestBase{

	private Logger logger = Logger.getLogger(Log4jMonitor_TEST.class);
	static{
		EntryIterator.IS_DEBUG = true;
	}
	
	@Override
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

	@Test
	public void simpleTest(){
		MonitorPointSelection mpSel = this.getMonitoredResult()
				.asSelection().select(".A > .D");
		TestUtils.assertMPAmount(mpSel, 1);
	}
	
	
}
