/**
 * 
 */
package org.zmonitor.impl;

import java.util.List;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.impl.MSPipe.Mode;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * 
 * Temporary implementation of Pipe concept.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MSPipeProvider {
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	
	public static MSPipe getPipe(String modeStr){
		return getPipe(Mode.getMode(modeStr));
	}
	
	/**
	 * 
	 * @param mode
	 * @return
	 */
	public static MSPipe getPipe(Mode mode){
		switch(mode){
		case SYNC:
			return new SyncPipe();
		case ASYNC:
			return new AsyncMSPipe();
		default:
			return new SyncPipe();
		}
		
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class SyncPipe extends ZMBeanBase implements MSPipe{

		private ZMonitorManager zMonitorManager;

		public void pipe(MonitorSequence ms) {
			List<MonitorSequenceHandler> handlers = 
					zMonitorManager.getBeans(MonitorSequenceHandler.class);
			for(MonitorSequenceHandler handler : handlers){
				handler.handle(ms);	
			}
		}

		public void configure(ConfigContext configCtx) {
			zMonitorManager = configCtx.getManager();
		}

		public Mode getMode() {
			return Mode.SYNC;
		}
		
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class AsyncMSPipe extends ZMBeanBase implements MSPipe{
		protected long waitMillis = -1;// don't wait..
		protected int threshold = 0;//no threshold
		protected AsyncGroupingPipe<MonitorSequence> asyncGroupPipe;
		
		public Mode getMode() {
			return Mode.ASYNC;
		}
		@Override
		protected void doStop() {
			asyncGroupPipe.flush();
		}
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
		
		public void pipe(MonitorSequence ms) {
			asyncGroupPipe.push(ms);
		}

		public void configure(final ConfigContext configCtx) {
			final ZMonitorManager manager = configCtx.getManager();
			asyncGroupPipe = new AsyncGroupingPipe<MonitorSequence>(threshold,
					waitMillis,
					new AsyncGroupingPipe.Executor<MonitorSequence>() {

						public void doSend(List<MonitorSequence> tls)
								throws Exception {
							
							List<MonitorSequenceHandler> handlers = 
								manager.getBeans(MonitorSequenceHandler.class);
							
							for(MonitorSequenceHandler handler : handlers){
								for(MonitorSequence ms : tls){
									handler.handle(ms);	
								}
							}
						}

						public boolean failover(Exception e,
								List<MonitorSequence> workingList) {
							ZMLog.warn(e, AsyncMSPipe.class,
									"got an error wile transmission, timeline were dropped off...");
							return true;
						}
					});
		}
	}//end of class...
	
}
