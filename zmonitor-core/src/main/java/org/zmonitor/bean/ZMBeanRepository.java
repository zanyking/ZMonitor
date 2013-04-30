/**
 * 
 */
package org.zmonitor.bean;

import java.util.Collection;

/**
 * A repository designed to manage {@link ZMBean} lifecycle<br> 
 * currently the implementation is very simple.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ZMBeanRepository {
	
	/**
	 * every bean that either is or instanceof the given class will be in the result.
	 * @param clz
	 * @return
	 */
	public <T> Collection<T> get(Class<T> clz);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T get(String id);
	
	
	/**
	 * add a new bean
	 * @param zmBean
	 */
	public void add(ZMBean zmBean);
	/**
	 * 
	 * @param id
	 */
	public void remove(String id);
	/**
	 * 
	 * @param zmBean
	 */
	public void remove( ZMBean zmBean);
	/**
	 * 
	 * @return true if this repository is already started.
	 */
	public boolean isStarted();
	/**
	 * 
	 * @return true if this repository is already stopped.
	 */
	public boolean isStopped();
	
	/**
	 * start every bean in this repository 
	 */
	public void start();

	/**
	 * stop every bean in this repository
	 */
	public void stop();
	

}
