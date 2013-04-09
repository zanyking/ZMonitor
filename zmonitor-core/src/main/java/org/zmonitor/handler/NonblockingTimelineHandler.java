/**
 * 
 */
package org.zmonitor.handler;

import org.w3c.dom.Node;
import org.zmonitor.Timeline;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.TimelineHandler;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

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
	 * @see org.zmonitor.CustomConfiguration#apply(org.zmonitor.ZMonitorManager, org.zmonitor.util.DOMRetriever, org.w3c.dom.Node)
	 */
	public void apply(final ZMonitorManager manager, DOMRetriever xmlDoc,
			Node configNode) {
		asyncGroupPipe = new AsyncGroupingPipe<Timeline>(
				threshold, waitMillis, newExecutor(manager));
	}
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.TimelineHandler#handle(org.zmonitor.Timeline)
	 */
	public void handle(Timeline tLine)  {
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
