/**
 * 
 */
package org.zmonitor.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * a thread safe implementation of bean repository
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ZMBeanRepositoryAbstract implements ZMBeanRepository{


	protected final List<ZMBean> beans = 
		Collections.synchronizedList(new ArrayList<ZMBean>());
	
	
	private volatile int status;// 0 constructed, 1 started, -1 stopped 
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
		if(isStopped())
			throw new IllegalStateException("already stopped");
		if(isStarted()){
			zmBean.start();
		}
		beans.add(zmBean);
	}
	/**
	 * 
	 * @param id
	 */
	public void remove(String id){
		ZMBean zmBean = get(id);
		if(zmBean!=null) remove(zmBean);
		//TODO handle null case
	}
	/**
	 * 
	 * @param zmBean
	 */
	public void remove( ZMBean zmBean){
		boolean b = beans.remove(zmBean);
		if(b)zmBean.stop();
	}
	/**
	 * 
	 * @return true if this repository is already started.
	 */
	public boolean isStarted(){
		return status==1;
	}
	/**
	 * 
	 * @return true if this repository is already stopped.
	 */
	public boolean isStopped(){
		return status<0;
	}
	
	/**
	 * start every bean in this repository 
	 */
	public synchronized void start(){
		if(status!=0)
			throw new IllegalStateException(status>0?
					"already started": "already stopped");
		doStart();//prepare context...
		for(ZMBean zmBean : beans){
			zmBean.start();
		}
		status = 1;
	}
	/**
	 * 
	 */
	protected abstract void doStart();

	/**
	 * stop every bean in this repository
	 */
	public synchronized void stop(){
		if(status<0)
			throw new IllegalStateException("already stopped");
		
		for(ZMBean zmBean : new ArrayList<ZMBean>(beans)){
			zmBean.stop();
		}
		doStop();// release resources...
		status = -1;
	}
	/**
	 * 
	 */
	protected abstract void doStop();
	
}
