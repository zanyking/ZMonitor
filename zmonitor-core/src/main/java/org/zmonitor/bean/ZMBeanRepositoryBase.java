/**
 * 
 */
package org.zmonitor.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zmonitor.impl.ZMLog;

/**
 * a thread safe implementation of bean repository
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ZMBeanRepositoryBase extends ZMBeanBase implements ZMBeanRepository{

	
	protected final List<ZMBean> beans = 
		Collections.synchronizedList(new ArrayList<ZMBean>());
	
	
	/**
	 * every bean that either is or instanceof the given class will be in the result.
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> get(Class<T> clz){
		
		ArrayList<T> arr = new ArrayList<T>();
		for(ZMBean bean : new ArrayList<ZMBean>(beans)){
			if(clz.isInstance(bean)){
				arr.add((T) bean);
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
		if(id==null || id.isEmpty())
			throw new IllegalArgumentException("id cannot be null or empty!");
		
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
		if(beans.contains(zmBean)){
			ZMLog.warn("cannot add zmBean multipletimes, operation ignored. zmbean: "+zmBean);
			return;
		}
		if(zmBean instanceof LifeCycle){
			LifeCycle lc = (LifeCycle) zmBean;
			if(lc.isStopped()){
				throw new IllegalArgumentException(
						"cannot accept a stopped bean: "+zmBean);
			}
			if(isStarted() && !lc.isStarted()){
				lc.start();
			}
		}
		beans.add(zmBean);
	}
	/**
	 * 
	 * @param id
	 */
	public void remove(String id){
		if(id==null || id.isEmpty())
			throw new IllegalArgumentException("id cannot be null or empty!");
		
		ZMBean zmBean = get(id);
		if(zmBean!=null){
			remove(zmBean);	
		}
		
		//TODO handle null case, currently do nothing...
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
			if(zmBean instanceof LifeCycle){
				LifeCycle lc = (LifeCycle) zmBean;
				if(lc.isStarted() && !lc.isStopped()){
					//only a started bean can be stopped.
					lc.stop();
				}
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
			if(zmBean instanceof LifeCycle){
				LifeCycle lc = (LifeCycle) zmBean;
				try{
					lc.start();	
				}catch(Throwable e){
					errs.add(e);
				}
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
		ArrayList<Throwable> errs = new ArrayList<Throwable>();
		for(ZMBean zmBean : new ArrayList<ZMBean>(beans)){
			if(zmBean instanceof LifeCycle){
				LifeCycle lc = (LifeCycle) zmBean;
				try{
					lc.stop();
				}catch(Throwable e){
					errs.add(e);
				}
			}
		}
		if(!errs.isEmpty()){
			throw new LifeCycleException(errs);
		}
	}
	
}
