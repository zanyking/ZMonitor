/**Configurator.java
 * 2011/4/4
 * 
 */
package org.zmonitor.spi;



/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Configurator {
	/**
	 * 
	 * @param manager
	 * @param configuration
	 */
	public void configure(ConfigContext configCtx);
}
