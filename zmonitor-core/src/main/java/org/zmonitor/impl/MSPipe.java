/**
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBean;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public interface MSPipe  extends ZMBean, CustomConfigurable{
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public enum Mode{
		SYNC("SYNC"),ASYNC("ASYNC");
		private String name;
		
		Mode(String name){
			this.name = name;
		}
		
		public static Mode getMode(String name){
			Mode mode = null;
			if(SYNC.name.equalsIgnoreCase(name)){
				mode = Mode.SYNC;
			}else if(ASYNC.name.equalsIgnoreCase(name)){
				mode = Mode.ASYNC;
			}else{
				throw new IllegalArgumentException(
						"must be \"SYNC\" or \"ASYNC\" mode:"+name);
			}
			return mode;
		}
	}
	/**
	 * 
	 * @param ms
	 */
	public void pipe(MonitorSequence ms);  
	/**
	 * 
	 * @return
	 */
	public Mode getMode();
}
