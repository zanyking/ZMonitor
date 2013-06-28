/**
 * 
 */
package zmonitor.test.clz.node;

import zmonitor.test.clz.node.TestNode.NodeFac;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class MessageFac implements NodeFac{
	protected String start_message = ">> method start";
	protected String end_message = "<< method end";
	protected MessageFac(){}
	public MessageFac(String start_message, String end_message) {
		this.start_message = start_message;
		this.end_message = end_message;
	}
}
