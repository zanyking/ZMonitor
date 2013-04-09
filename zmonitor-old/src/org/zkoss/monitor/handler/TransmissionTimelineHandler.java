/**
 * 
 */
package org.zkoss.monitor.handler;

import java.util.List;

import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.message.NewTimelineMessage;
import org.zkoss.monitor.util.concurrent.AsyncGroupingPipe;
import org.zkoss.monitor.util.concurrent.AsyncGroupingPipe.Executor;


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
