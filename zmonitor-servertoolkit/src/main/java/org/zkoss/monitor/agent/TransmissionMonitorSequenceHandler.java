/**
 * 
 */
package org.zkoss.monitor.agent;

import java.util.List;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.MonitorSequenceMessage;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;
import org.zmonitor.util.concurrent.AsyncGroupingPipe.Executor;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TransmissionMonitorSequenceHandler extends NonblockingMonitorSequenceHandler {
	

	@Override
	protected Executor<MonitorSequence> newExecutor(final ZMonitorManager manager) {
		return new AsyncGroupingPipe.Executor<MonitorSequence>() {
			
			public void flush(List<MonitorSequence> tls) throws Exception{
				MonitorSequenceMessage mesg = new MonitorSequenceMessage();
				mesg.add(tls);
//				manager.getAgent().getTransmitter().send(mesg);
				//TODO: find a way to instantiate an Agent instance and use it to send message.
			}
			
			public boolean failover(Exception e, List<MonitorSequence> workingList) {
				ZMLog.warn(e, TransmissionMonitorSequenceHandler.class, 
						"got an error wile transmission, timeline were dropped off...");
				return true;//
			}
		};
	}


}
