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
public abstract class ZMBeanRepositoryBase extends LifeCycleBase implements ZMBeanRepository{


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
		if(isStopped()){
			throw new IllegalStateException(
					"operate a stopped repo is pointless");
		}
		if(zmBean.isStopped()){
			throw new IllegalArgumentException(
					"cannot accept a stopped bean: "+zmBean);
		}
		if(isStarted() && !zmBean.isStarted()){
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
		if(zmBean!=null){
			remove(zmBean);	
		}
		
		//TODO handle null case
	}
	/**
	 * 
	 * @param zmBean
	 */
	public void remove( ZMBean zmBean){
		if(isStopped()){
			throw new IllegalStateException(
					"operate a stopped repo is pointless");
		}
		boolean b = beans.remove(zmBean);
		if(b){
			if(zmBean.isStarted() && !zmBean.isStopped()){
				//only a started bean can be stopped.
				zmBean.stop();
			}
		}else{
			throw new IllegalArgumentException(
					"the given bean is not belongs to this repo.");
		}
	}
	
	/**
	 * 
	 */
	protected void doStart(){
		ArrayList<Throwable> errs = new ArrayList<Throwable>();
		for(ZMBean zmBean : beans){
			try{
				zmBean.start();	
			}catch(Throwable e){
				errs.add(e);
				e.printStackTrace();
			}
		}
		if(!errs.isEmpty()){
			throw new LifeCycleException(errs);
		}
	}

	/**
	 * 
	 */
	protected void doStop(){
		for(ZMBean zmBean : new ArrayList<ZMBean>(beans)){
			zmBean.stop();
		}
	}
	
}
