/**
 * 
 */
package org.zmonitor.handler;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class NonblockingMonitorSequenceHandler extends ZMBeanBase implements MonitorSequenceHandler, CustomConfigurable{

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
	 * @see org.zmonitor.CustomConfigurable#configure(org.zmonitor.config.ConfigContext)
	 */
	public void configure(ConfigContext configCtx) {
		asyncGroupPipe = new AsyncGroupingPipe<MonitorSequence>(
				threshold, waitMillis, newExecutor(configCtx.getManager()));
	}
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.spi.MonitorSequenceHandler#handle(org.zmonitor.Timeline)
	 */
	public void handle(MonitorSequence tLine)  {
		asyncGroupPipe.push(tLine);
	}

	public void destroy() {
		asyncGroupPipe.flush();
	}
}
