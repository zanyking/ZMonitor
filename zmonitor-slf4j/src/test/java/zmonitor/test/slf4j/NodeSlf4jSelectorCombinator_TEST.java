/**
 * 
 */
package zmonitor.test.slf4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zmonitor.test.junit.TestBase;

import zmonitor.test.slf4j.clz.node.A;
import zmonitor.test.slf4j.clz.node.B;
import zmonitor.test.slf4j.clz.node.C;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class NodeSlf4jSelectorCombinator_TEST  extends TestBase{
	private static final Logger logger = 
			LoggerFactory.getLogger(NodeSlf4jMPSelector_TEST.class);
	@Test
	public void test_siblingParentPriority() throws Exception {
		logger.info(">> start test case");
		A root = new A();
		root.toFirstChild(B.B_FAC)
				.toFirstChild(B.B_FAC)
				.toNextSibling(C.C_FAC.toCFac(
						"test message, ID:{}, isEngineer:{}, name:{}, assignment:{}", 
						7, true,"Robbie Cheng", "Client Side Engine enhancement"))
				.toNextSibling(C.C_FAC.toCFac(
						"test message, ID:{}, isEngineer:{}, name:{}, assignment:{}", 
						8, true,"Jumper Cheng", "Marketing, Product and Solution providing"))
				.toNextSibling(C.C_FAC.toCFac(
						"test message, ID:{}, isEngineer:{}, name:{}, assignment:{}", 
						9, true,"Ian Tsai", "Serverside Java EE solution & integration"))
				.getParent()
			.toNextSibling(B.B_FAC)
			.toNextSibling(A.A_FAC);
		root.doNode();
		logger.info("<< end test case");
	
		Slf4jTestUtils.testEntryIterator(".A .B + .C", 1, 
				this.getMonitoredResult());//[message.0=9][message.2*='Ian']
	}
}
