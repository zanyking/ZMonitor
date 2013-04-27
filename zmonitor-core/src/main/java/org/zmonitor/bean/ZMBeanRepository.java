/**
 * 
 */
package org.zmonitor.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A repository designed to manage {@link ZMBean} lifecycle<br> 
 * currently the implementation is very simple.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ZMBeanRepository {
	
	protected final List<ZMBean> beans = 
		Collections.synchronizedList(new ArrayList<ZMBean>());
	
	/**
	 * every bean that either is or instanceof the given class will be in the result.
	 * @param clz
	 * @return
	 */
	public <T> Collection<T> get(Class<T> clz){
		ArrayList<T> arr = new ArrayList<T>();
		for(ZMBean b : new ArrayList<ZMBean>(beans)){
			if(clz.isInstance(b)){
				arr.add((T) b);
			}
		}
		return arr;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public <T> T get(String id){
		for(ZMBean b : new ArrayList<ZMBean>(beans)){
			if(id.equals(b.getId()))return (T) b;
		}
		return null;
	}
	
	
	/**
	 * add a new bean
	 * @param zmBean
	 */
	public void add(ZMBean zmBean){
		beans.add(zmBean);
	}

	/**
	 * 
	 * @return true if this repository is started.
	 */
	public abstract boolean isStarted();
	/**
	 * start every bean in this repository 
	 */
	public abstract void start();

	/**
	 * stop every bean in this repository
	 */
	public abstract void stop();
	
	
	

}
