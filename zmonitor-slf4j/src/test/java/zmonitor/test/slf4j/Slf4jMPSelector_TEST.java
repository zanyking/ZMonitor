/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		Slf4jTestUtils.testEntryIterator(".BusinessObject .Dao.getBean:greater-than(END, 50)", 1, 
				this.getMonitoredResult());
	}
	@Test
	public void test_PseudoClass2() throws Exception {
		Slf4jTestUtils.testEntryIterator(".Dao.getBean:greater-than(END, 50)", 2, 
				this.getMonitoredResult());
	}
	@Test
	public void test_P2() throws Exception {
		Slf4jTestUtils.testEntryIterator(".Service .Dao.getBean[message*='user']", 1, 
				this.getMonitoredResult());
	}
	@Test
	public void test_Attribute() throws Exception {
		Slf4jTestUtils.testEntryIterator("[message.0*='Ian']", 2, 
				this.getMonitoredResult());
	}
	@Test
	public void test_Attribute2() throws Exception {
		Slf4jTestUtils.testEntryIterator(".BusinessObject [message.1=12]", 1, 
				this.getMonitoredResult());
	}
	
}
