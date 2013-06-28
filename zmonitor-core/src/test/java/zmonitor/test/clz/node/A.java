/**
 * 
 */
package zmonitor.test.clz.node;

import org.apache.log4j.Logger;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class A extends TestNode {

	public A() {
		super(null, null);
	}
	public A(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}


	public static final NodeFac A_FAC = new NodeFac(){
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			return new A(parent, previousSibling);
		}
	};

	Logger logger = Logger.getLogger(A.class);
	@Override
	protected void selfStart() {
		logger.debug(">> doA() ");
	}


	@Override
	protected void selfEnd() {
		logger.debug("<< doA()  ");
	}
}
