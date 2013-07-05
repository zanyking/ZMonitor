/**
 * 
 */
package org.zmonitor.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zmonitor.impl.ZMLog;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AsyncGroupingPipe<T> {

	protected final List<T> repository = 
		Collections.synchronizedList(new LinkedList<T>());
	
	private final AsyncInsensitiveAction fInsensitiveAction;
	protected int threshold;
	/**
	 * trigger doSend after wait for milliseconds, no mater how much data still in repository.   
	 * @param waitMillis set -1 to disable it.
	 */
	public AsyncGroupingPipe(long waitMillis, final Executor<T> exec){
		this(0, waitMillis, exec);
	}
	/**
	 * 
	 * @param thershold
	 * @param waitMillis
	 * @param exec
	 */
	public AsyncGroupingPipe(int thershold, long waitMillis, final Executor<T> exec){
		this.threshold = thershold;
		fInsensitiveAction = new AsyncInsensitiveAction(waitMillis) {
			protected Runnable getAction() {
				return new Runnable() {
					public void run() {
						ZMLog.debug("start to consume repository: "+repository.size());
						ArrayList<T> workingList = new ArrayList<T>(repository);
						if(workingList.isEmpty())return;
						
						boolean shouldRemove = false;
						try {
							exec.flush(workingList);
							shouldRemove = true;
						} catch (Exception e) {
							shouldRemove = exec.failover(e, workingList);
						}
						if(shouldRemove){
							repository.removeAll(workingList);	
						}						
					}
				};
			}
			protected boolean reachThreshold(){
				return AsyncGroupingPipe.this.reachThreshold();
			}
		};
	}


	/**
	 * add record for transit.
	 * @param obj
	 */
	public void push(T obj){
		repository.add(obj);
		fInsensitiveAction.trigger();
	}
	/**
	 * 
	 */
	public void flush(){
		fInsensitiveAction.invoke();
	}
	/**
	 * implement your own threshold detection implementation.
	 * @return
	 */
	protected boolean reachThreshold() {
		return repository.size() > threshold;
	}
	/**
	 * 
	 * @return
	 */
	public int getThreshold(){
		return this.threshold;
	}
	/**
	 * 
	 * @param threshold
	 */
	public void setThreshold(int threshold){
		this.threshold = threshold;
	}
	/**
	 * 
	 * @return
	 */
	public long getWaitMillis() {
		return fInsensitiveAction.getWaitMillis();
	}
	/**
	 * 
	 * @param waitMillis
	 */
	public void setWaitMillis(long waitMillis) {
		fInsensitiveAction.setWaitMillis(waitMillis);
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public interface Executor<T>{
		/**
		 * The workhorse that will send the grouped objects to the possible destination.<br>
		 * This method will be invoked in a thread safe env, so the implementation doesn't has to be thread safe. 
		 * 
		 * @param objs
		 * @throws Exception if anything cause transmission failed, 
		 * implementor should throw exception and handle the mass 
		 * through {@link AsyncGroupingPipe#failover(Exception)}
		 */
		public void flush(List<T> objs) throws Exception;
		/**
		 * 
		 * @param e
		 * @param workingList the processing items
		 * @return true if you want to disregard these items, false these items will be processed next time.
		 */
		public boolean failover(Exception e, List<T> workingList);
	}//end of class...
	


}
