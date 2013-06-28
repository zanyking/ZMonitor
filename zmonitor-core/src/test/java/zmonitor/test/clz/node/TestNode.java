/**
 * 
 */
package zmonitor.test.clz.node;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class TestNode {
	
	private final TestNode parent;
	private final TestNode previousSibling;
	private TestNode firstChild;
	private TestNode nextSibling;


	public TestNode(TestNode parent,  TestNode previousSibling) {
		this.parent = parent;
		this.previousSibling = previousSibling;
	}
	public TestNode getParent() {
		return parent;
	}
	public TestNode getFirstChild() {
		return firstChild;
	}
	public TestNode getNextSibling() {
		return nextSibling;
	}
	public TestNode getPreviousSibling() {
		return previousSibling;
	}
	
	public TestNode toNextSibling(NodeFac nodeFac){
		TestNode node = nodeFac.newNode(parent, previousSibling);
		this.nextSibling = node;
		return node;
	}

	public TestNode toFirstChild(NodeFac nodeFac){
		TestNode node = nodeFac.newNode(this, null);
		this.firstChild = node;
		return node;
	}
	
	public void doNode(){
		selfStart();
		if(firstChild!=null){
			firstChild.doNode();
		}
		selfEnd();
		
		if(nextSibling!=null){
			nextSibling.doNode();
		}
	}
	
	
	protected abstract void selfStart() ;
	protected abstract void selfEnd() ;


	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public interface NodeFac{
		TestNode newNode(TestNode parent, TestNode previousSibling);
	}
	
	
}
