/**
 * 
 */
package org.zmonitor.web.test;

import java.util.List;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.handler.NonblockingMonitorSequenceHandler;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.MonitorSequenceMessage;
import org.zmonitor.message.Transmitter;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;
import org.zmonitor.util.concurrent.AsyncGroupingPipe.Executor;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TransmissionMonitorSequenceHandler extends NonblockingMonitorSequenceHandler {
	
	private Transmitter transmitter;
	
	public Transmitter getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(Transmitter transmitter) {
		this.transmitter = transmitter;
	}

	@Override
	protected Executor<MonitorSequence> newExecutor(final ZMonitorManager manager) {
		return new AsyncGroupingPipe.Executor<MonitorSequence>() {
			
			public void flush(List<MonitorSequence> tls) throws Exception{
				MonitorSequenceMessage mesg = new MonitorSequenceMessage();
				mesg.add(tls);
				getTransmitter().send(mesg);//Blocking I/O.
			}
			
			public boolean failover(Exception e, List<MonitorSequence> workingList) {
				ZMLog.warn(e, TransmissionMonitorSequenceHandler.class, 
						"got an error wile transmission, timeline were dropped off...");
				return true;//
			}
		};
	}


}
