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
	 * @param appenderCtx
	 */
	 void hookUpCustomAppender(ConfigContext appenderCtx);
}
