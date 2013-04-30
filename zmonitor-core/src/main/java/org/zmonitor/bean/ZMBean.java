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
	
	/**
	 * 
	 * @return an identifier of this bean
	 */
	public String getId();
	/**
	 * to start a bean, should be called by repository.  
	 */
	public void start();
	/**
	 * to stop a bean, should be called by repository.  
	 */
	public void stop();
	/**
	 * 
	 * @return test if this bean is started, an none-started bean's operation is limited.
	 */
	public boolean isStarted();
	/**
	 * 
	 * @return test if this bean is stopped, a stopped bean's operation is limited.
	 */
	public boolean isStopped();
	
	
}
