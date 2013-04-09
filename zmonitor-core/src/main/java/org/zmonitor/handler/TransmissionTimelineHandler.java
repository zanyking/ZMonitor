/**
 * 
 */
package org.zmonitor.handler;

import java.util.List;

import org.zmonitor.Timeline;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.NewTimelineMessage;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;
import org.zmonitor.util.concurrent.AsyncGroupingPipe.Executor;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TransmissionTimelineHandler extends NonblockingTimelineHandler {
	

	@Override
	protected Executor<Timeline> newExecutor(final ZMonitorManager manager) {
		return new AsyncGroupingPipe.Executor<Timeline>() {
			
			public void doSend(List<Timeline> tls) throws Exception{
				NewTimelineMessage mesg = new NewTimelineMessage();
				mesg.add(tls);
				manager.getAgent().getTransmitter().send(mesg);	
			}
			
			public boolean failover(Exception e, List<Timeline> workingList) {
				ZMLog.warn(e, TransmissionTimelineHandler.class, 
						"got an error wile transmission, timeline were dropped off...");
				return true;//
			}
		};
	}

}
