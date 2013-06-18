/**
 * 
 */
package zmonitor.test.slf4j.clz.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class B extends Slf4jTestNode {
	private static final Logger logger = LoggerFactory.getLogger(B.class);
	public B(Slf4jTestNode parent, Slf4jTestNode previousSibling) {
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
		public Slf4jTestNode newNode(Slf4jTestNode parent, Slf4jTestNode previousSibling) {
			return new B(parent, previousSibling);
		}

	};
}
