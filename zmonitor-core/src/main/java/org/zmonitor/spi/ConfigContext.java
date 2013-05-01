/**
 * 
 */
package org.zmonitor.spi;

import org.zmonitor.ZMonitorManager;



/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ConfigContext {
	/**
	 * attributes the config.properties provided.
	 * @param key the key to access property
	 * @return value 
	 */
	public String get(String key);
	/**
	 * 
	 * @return the manager that Configurator should manipulated.
	 */
	public ZMonitorManager getManager();
	/**
	 * 
	 * @return
	 */
	public XMLConfiguration getConfiguration();
}






