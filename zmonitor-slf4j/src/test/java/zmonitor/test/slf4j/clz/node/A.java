/**
 * 
 */
package zmonitor.test.slf4j.clz.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zmonitor.test.clz.node.TestNode;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class A extends TestNode {

	private static final Logger logger = LoggerFactory.getLogger(A.class);
	
	
	public A(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}

	public A(){
		this(null,null);
	}
	protected void selfStart() {
		logger.trace(">> start doA()");
		
	}
	protected void selfEnd() {
		logger.trace("<< end doA()");
	}
	
	
	public static final NodeFac A_FAC = new NodeFac(){
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			return new A(parent, previousSibling);
		}
	};


	
}
