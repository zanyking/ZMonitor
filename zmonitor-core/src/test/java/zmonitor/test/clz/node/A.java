/**
 * 
 */
package zmonitor.test.clz.node;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class A extends TestNode {

	public A(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}

	/* (non-Javadoc)
	 * @see zmonitor.test.clz.node.Node#selfTask()
	 */
	@Override
	protected void selfTask() {
		
	}

	public static final NodeFac A_FAC = new NodeFac(){
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			return new A(parent, previousSibling);
		}
	};
}
