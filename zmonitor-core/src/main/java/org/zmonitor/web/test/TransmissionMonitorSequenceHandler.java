/**
 * 
 */
package org.zmonitor.web.test;

import java.util.List;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Communicator;
import org.zmonitor.message.MonitorSequenceMessage;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;
import org.zmonitor.util.concurrent.AsyncGroupingPipe.Executor;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TransmissionMonitorSequenceHandler extends ZMBeanBase implements MonitorSequenceHandler {
	
	private AsyncGroupingPipe<MonitorSequence> asyncGroupPipe;
	
	private Communicator communicator;
	
	public Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public void init(final ZMonitorManager manager){
		asyncGroupPipe = new AsyncGroupingPipe<MonitorSequence>(
				0, -1, newExecutor(manager));
	}

	protected Executor<MonitorSequence> newExecutor(final ZMonitorManager manager) {
		return new AsyncGroupingPipe.Executor<MonitorSequence>() {
			public void flush(List<MonitorSequence> tls) throws Exception{
				MonitorSequenceMessage mesg = new MonitorSequenceMessage();
				mesg.add(tls);
				//no callback, hasCallback at webtest side is false.
				communicator.getTransmitter().send(mesg);//Blocking I/O.
			}
			public boolean failover(Exception e, List<MonitorSequence> workingList) {
				ZMLog.warn(e, TransmissionMonitorSequenceHandler.class, 
						"got an error wile transmission, monitor sequence were dropped ...");
				return true;//
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.spi.MonitorSequenceHandler#handle(org.zmonitor.Timeline)
	 */
	public void handle(MonitorSequence ms)  {
		asyncGroupPipe.push(ms);
	}

	public void destroy() {
		asyncGroupPipe.flush();
	}
}
