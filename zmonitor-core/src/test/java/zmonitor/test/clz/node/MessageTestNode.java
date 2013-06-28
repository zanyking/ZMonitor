/**
 * 
 */
package zmonitor.test.clz.node;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class MessageTestNode extends TestNode {
	
	
	protected String startMessage;
	protected String endMessage;
	
	
	public MessageTestNode(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}
	

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public void setEndMessage(String endMessage) {
		this.endMessage = endMessage;
	}


}
