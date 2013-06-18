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
public class A extends Slf4jTestNode {

	private static final Logger logger = LoggerFactory.getLogger(A.class);
	
	
	public A(Slf4jTestNode parent, Slf4jTestNode previousSibling) {
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
		public Slf4jTestNode newNode(Slf4jTestNode parent, Slf4jTestNode previousSibling) {
			return new A(parent, previousSibling);
		}
	};


	
}
