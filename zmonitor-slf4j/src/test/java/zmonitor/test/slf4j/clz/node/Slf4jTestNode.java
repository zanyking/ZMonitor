/**
 * 
 */
package zmonitor.test.slf4j.clz.node;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class Slf4jTestNode {
	
	private final Slf4jTestNode parent;
	private final Slf4jTestNode previousSibling;
	private Slf4jTestNode firstChild;
	private Slf4jTestNode nextSibling;


	public Slf4jTestNode(Slf4jTestNode parent,  Slf4jTestNode previousSibling) {
		this.parent = parent;
		this.previousSibling = previousSibling;
	}
	public Slf4jTestNode getParent() {
		return parent;
	}
	public Slf4jTestNode getFirstChild() {
		return firstChild;
	}
	public Slf4jTestNode getNextSibling() {
		return nextSibling;
	}
	public Slf4jTestNode getPreviousSibling() {
		return previousSibling;
	}
	
	public Slf4jTestNode toNextSibling(NodeFac nodeFac){
		Slf4jTestNode node = nodeFac.newNode(parent, previousSibling);
		this.nextSibling = node;
		return node;
	}
	public Slf4jTestNode toFirstChild(NodeFac nodeFac){
		Slf4jTestNode node = nodeFac.newNode(this, null);
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
		Slf4jTestNode newNode(Slf4jTestNode parent, Slf4jTestNode previousSibling);
	}
	
	
}
