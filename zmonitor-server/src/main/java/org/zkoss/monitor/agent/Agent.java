/**Master.java
 * 2011/10/11
 * 
 */
package org.zkoss.monitor.agent;

import org.w3c.dom.Node;
import org.zkoss.monitor.impl.JObjSStreamCommunicator;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.message.Communicator;
import org.zmonitor.message.Receiever;
import org.zmonitor.message.Transmitter;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.util.DOMRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Agent implements Communicator, CustomConfiguration{
	private String host;// domain name,
	private int port= -1;// >=0, <= 65535	
	
	private Communicator fCommunicator; 
	
	/**
	 * 
	 * @param zMgmt
	 */
	public Agent(){
	}
	
	public String getMasterHost() {
		return host;
	}

	public void setMasterHost(String host) {
		this.host = host;
	}

	public int getMasterPort() {
		return port;
	}

	public void setMasterPort(int port) {
		if(port<0 || port >65535){
			this.port = -1;//assignment port is not correct, set port to -1. 
			throw new IllegalArgumentException("the given port is incorrect: "+port);	
		}
		this.port = port;
	}
	
	public void apply(ZMonitorManager manager, DOMRetriever xmlDoc,
			Node configNode) {
		fCommunicator = new JObjSStreamCommunicator(host, port);
	}
	

	public Transmitter getTransmitter() {
		return fCommunicator.getTransmitter();
	}

	public Receiever getReceiever() {
		return fCommunicator.getReceiever();
	}

	

	
}
