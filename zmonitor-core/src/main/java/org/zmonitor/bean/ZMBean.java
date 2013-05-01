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
public interface ZMBean extends LifeCycle{
	
	/**
	 * 
	 * @return an identifier of this bean
	 */
	public String getId();
	/**
	 * 
	 * @return
	 */
	public void setId(String id);
	
}
