/**
 * 
 */
package org.zmonitor.bean;

/**
 * 
 * A bean managed by {@link ZMBeanRepository}, a bean can be started, stopped. 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ZMBean {
	
	public String getId();
	public void start(ZMBeanContext bCtxt);
	public void stop();
	public boolean isStarted();
	
	
}
