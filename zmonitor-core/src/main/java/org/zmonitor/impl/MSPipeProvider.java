/**
 * 
 */
package org.zmonitor.impl;

import java.util.List;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBean;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MSPipeProvider {
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public enum Mode{
		SYNC,ASYNC
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public interface MSPipe extends ZMBean, CustomConfigurable{
		/**
		 * 
		 * @param ms
		 */
		public void pipe(MonitorSequence ms);  
	}//end of class...
	
	
	public static MSPipe getPipe(String modeStr){
		Mode mode = null;
		if("sync".equalsIgnoreCase(modeStr))mode = Mode.SYNC;
		else if("async".equalsIgnoreCase(modeStr))mode = Mode.ASYNC;
		else{
			throw new IllegalArgumentException("must be \"SYNC\" or \"ASYNC\" mode:"+modeStr);
		}
		return getPipe(mode);
		
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
