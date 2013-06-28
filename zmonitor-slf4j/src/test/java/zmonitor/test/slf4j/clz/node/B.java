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
public class B extends TestNode {
	private static final Logger logger = LoggerFactory.getLogger(B.class);
	public B(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}

	public B() {
		this(null,null);
	}

	protected void selfStart() {
		doB1Start();
	}
	protected void selfEnd() {
		doB1End();
	}

	
	private void doB1Start(){
		logger.trace(">> start doB()");
		
	}
	private void doB1End(){
		logger.trace("<< end of doB().");
	}
	
	public static final NodeFac B_FAC = new NodeFac(){
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			return new B(parent, previousSibling);
		}

	};
}
