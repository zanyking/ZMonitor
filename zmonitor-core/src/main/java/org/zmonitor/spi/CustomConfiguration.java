/**CustomConfiguration.java
 * 2011/4/5
 * 
 */
package org.zmonitor.spi;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.util.DOMRetriever;

/**
 * A way to let your ZMonitor Components be configurable.<br> 
 * A component(by now {@link MonitorSequenceHandler}, {@link CustomConfiguration} and {@link MonitorPointInfoFactory}) which implements this interface and declared in zmonitor.xml will has a chance to retrieve it's sub XML elements to configure it's self.<br>  
 * please take a look at {@link #apply(ZMonitorManager, DOMRetriever, Node)} method's javadoc, and {@link org.zmonitor.impl.XMLConfigurator XMLConfigurator}'s constants to get detail.
 * 
 * @see MonitorSequenceHandler
 * @see MonitorPointInfoFactory
 * @author Ian YT Tsai(Zanyking)
 */
public interface CustomConfiguration {
	/**
	 * 
	 * @param manager
	 * @param xmlDoc
	 * @param configNode
	 */
	public void apply(ZMonitorManager manager, DOMRetriever xmlDoc, Node configNode);
	
	
}
