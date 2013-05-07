/**
 * 2011/4/4
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.config.ConfigContext;



/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Configurator {
	/**
	 * 
	 * @param configCtx the initial DOM node selection is &lt;zmonitor> 
	 */
	public void configure(ConfigContext configCtx);
}
