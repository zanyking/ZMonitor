/**
 * 
 */
package org.zkoss.monitor.handler;

import org.w3c.dom.Node;
import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.spi.CustomConfiguration;
import org.zkoss.monitor.spi.TimelineHandler;
import org.zkoss.monitor.util.DOMRetriever;
import org.zkoss.monitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class NonblockingTimelineHandler implements TimelineHandler, CustomConfiguration{

	protected AsyncGroupingPipe<Timeline> asyncGroupPipe;
	
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
	protected abstract AsyncGroupingPipe.Executor<Timeline> newExecutor(ZMonitorManager manager);
	
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.monitor.CustomConfiguration#apply(org.zkoss.monitor.ZMonitorManager, org.zkoss.monitor.util.DOMRetriever, org.w3c.dom.Node)
	 */
	public void apply(final ZMonitorManager manager, DOMRetriever xmlDoc,
			Node configNode) {
		asyncGroupPipe = new AsyncGroupingPipe<Timeline>(
				threshold, waitMillis, newExecutor(manager));
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.monitor.TimelineHandler#handle(org.zkoss.monitor.Timeline)
	 */
	public void handle(Timeline tLine)  {
		asyncGroupPipe.push(tLine);
	}
	/*
	 * (non-Javadoc)
	 * @see org.zkoss.monitor.TimelineHandler#destroy()
	 */
	public void destroy() {
		asyncGroupPipe.flush();
	}
}
