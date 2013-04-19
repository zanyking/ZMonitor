/**
 * 
 */
package org.zmonitor.handler;

import org.w3c.dom.Node;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class NonblockingMonitorSequenceHandler implements MonitorSequenceHandler, CustomConfiguration{

	protected AsyncGroupingPipe<MonitorSequence> asyncGroupPipe;
	
	protected long waitMillis = -1;
	protected int threshold = 0;
	/**
	 * 
	 * @return
	 */
	public long getWaitMillis() {
		return waitMillis;
	}
	/**
	 * waitMillis is designed for 
	 * 
	 * @param waitMillis
	 */
	public void setWaitMillis(long waitMillis) {
		this.waitMillis = waitMillis;
	}
	/**
	 * 
	 * @return
	 */
	public int getThreshold() {
		return threshold;
	}
	/**
	 * 
	 * @param threshold
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	/**
	 * 
	 * @param manager 
	 * @return
	 */
	protected abstract AsyncGroupingPipe.Executor<MonitorSequence> newExecutor(ZMonitorManager manager);
	
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.CustomConfiguration#apply(org.zmonitor.ZMonitorManager, org.zmonitor.util.DOMRetriever, org.w3c.dom.Node)
	 */
	public void apply(final ZMonitorManager manager, DOMRetriever xmlDoc,
			Node configNode) {
		asyncGroupPipe = new AsyncGroupingPipe<MonitorSequence>(
				threshold, waitMillis, newExecutor(manager));
	}
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.spi.MonitorSequenceHandler#handle(org.zmonitor.Timeline)
	 */
	public void handle(MonitorSequence tLine)  {
		asyncGroupPipe.push(tLine);
	}
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.TimelineHandler#destroy()
	 */
	public void destroy() {
		asyncGroupPipe.flush();
	}
}
