/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.config.ConfigContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Driver {
	/**
	 * 
	 * @param hookupCtx
	 */
	 void hookUpCustomAppender(HookUpContext hookupCtx);
	 /**
	  * 
	  * @author Ian YT Tsai (Zanyking)
	  *
	  */
	 interface HookUpContext{
		 ConfigContext getAppenderCtx();
		 String getLogLevel();
	 }
}
