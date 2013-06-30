package zmonitor.test.log4j.clz.node;

import org.apache.log4j.Logger;
import org.zmonitor.util.Strings;

import zmonitor.test.clz.node.MessageFac;
import zmonitor.test.clz.node.MessageTestNode;
import zmonitor.test.clz.node.TestNode;
import zmonitor.test.log4j.clz.node.D.DFac;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class E extends MessageTestNode {

	public E(TestNode parent, TestNode previousSibling) {
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
	public static class EFac extends MessageFac{
		private EFac(){}
		public EFac(String start_message, String end_message) {
			super(start_message,end_message);
		}
		
		public TestNode newNode(TestNode parent, TestNode previousSibling) {
			E node = new E(previousSibling, previousSibling);
			node.setStartMessage(start_message);
			node.setEndMessage(end_message);
			return node;
		}
		public EFac newFac(String start_message, String end_message) {
			return new EFac(start_message,end_message);
		}
	}
	public static final EFac E_FAC = new EFac();
}
