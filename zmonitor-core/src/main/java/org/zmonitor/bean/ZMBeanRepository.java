/**
 * 
 */
package org.zmonitor.bean;

import java.util.List;

/**
 * A repository designed to manage {@link ZMBean} lifecycle<br> 
 * currently the implementation is very simple.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ZMBeanRepository extends ZMBean, LifeCycle{
	
	/**
	 * every bean that either is or instanceof the given class will be in the result.
	 * @param clz
	 * @return
	 */
	public <T> List<T> get(Class<T> clz);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T get(String id);
	
	
	/**
	 * add a bean into repo.<br>
	 * 
	 * if the repo is started, this method will try to start the bean before 
	 * adding.<br>
	 * a stopped 
	 * @param zmBean
	 * @throws IllegalArgumentException if bean is stopped
	 * @throws IllegalStateException if this repo is stopped
	 */
	public void add(ZMBean zmBean);
	/**
	 * same as {@link #remove(ZMBean)}, 
	 * but if the given id has no value, this method takes no effect. 
	 * 
	 * @param id 
	 */
	public void remove(String id);
	/**
	 *
	 * @param zmBean
	 * @throws IllegalStateException if this repo is stopped
	 * @throws IllegalArgumentException if the given bean is not belongs to this repo
	 */
	public void remove( ZMBean zmBean);

}
