/**
 * 2011/4/5
 */
package org.zmonitor;

import org.zmonitor.bean.ZMBean;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.MonitorSequenceHandler;

/**
 * A way to let your ZMonitor Components be configurable.<br> 
 * A component(by now {@link MonitorSequenceHandler}) which implements this interface and is declared in zmonitor.xml 
 * will be able to retrieve sub XML elements to configure it's self while ZMonitor configuration phase.<br>  
 * please take a look at {@link #configure(ConfigContext)} method's javadoc.
 * 
 * @see MonitorSequenceHandler
 * @author Ian YT Tsai(Zanyking)
 */
public interface CustomConfigurable extends ZMBean{
	/**
	 * 
	 * @param configCtx
	 */
	public void configure(ConfigContext configCtx);
	
	
}
