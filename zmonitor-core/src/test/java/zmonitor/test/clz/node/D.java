/**
 * 
 */
package zmonitor.test.clz.node;

import org.apache.log4j.Logger;
import org.zmonitor.util.Strings;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class D extends MessageTestNode {

	public D(TestNode parent, TestNode previousSibling) {
		super(parent, previousSibling);
	}
	protected void selfStart() {
		if(!Strings.isEmpty(startMessage))
			Logger.getLogger(this.getClass()).debug(startMessage);
	}

	protected void selfEnd() {
		if(!Strings.isEmpty(endMessage))
			Logger.getLogger(this.getClass()).debug(endMessage);
	}
	
	public static class DFac extends MessageFac{
		private DFac(){}
		public DFac(String start_message, String end_message) {
			super(start_message,end_message);
		}
		
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			D d = new D(previousSibling, previousSibling);
			d.setStartMessage(start_message);
			d.setEndMessage(end_message);
			return d;
		}
		public DFac newFac(String start_message, String end_message) {
			return new DFac(start_message,end_message);
		}
	}
	
	public static final DFac D_FAC = new DFac();

	
	
}
