/**
 * 2011/4/1
 * 
 */
package org.zmonitor.impl;

import java.util.HashMap;
import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycleManager;

/**
 * 
 * this implementation will manage the {@link MonitorSequence} instance 
 * {@inheritDoc}<br> 
 */
public class SimpleMonitorLifecycle implements MonitorLifecycle {
	
	protected MonitorSequence mSequence;
	protected boolean finished;
	protected MonitorLifecycleManager lfcManager;
	protected MonitorState mState;
	/**
	 * 
	 * @param lfcManager
	 */
	public SimpleMonitorLifecycle( MonitorLifecycleManager lfcManager) {
		this.lfcManager = lfcManager;
		mState = new MonitorStateImpl();
	}
	public MonitorSequence init() {
		if(finished)
			throw new IllegalStateException("this life-cycle was already finished, should never be reused.");
		if(getMonitorSequence() == null){
			setMonitorSequence(new MonitorSequence());
		}
		return getMonitorSequence();
	}
	protected void setMonitorSequence(MonitorSequence mSequence) {
		if(finished)
			throw new IllegalStateException("this life-cycle was already finished, should never be reused.");
		this.mSequence = mSequence;
	}
	public MonitorSequence getMonitorSequence() {
		return mSequence;
	}
	public boolean isInitialized() {
		return mSequence!=null;
	}
	public boolean isMonitorStarted() {
		if(!isInitialized())return false;
		return mSequence.isStarted();
	}
	public void finish() {
		if(finished)
			throw new IllegalStateException("this life-cycle was already " +
					"finished, should never be reused.");
		
		mSequence.setSize(mState.size());
		try{
			ZMonitorManager.getInstance().handle(mSequence);
			lfcManager.disposeLifecycle(this);
		}finally{
			mSequence = null;
			finished = true;	
		}
	}
	public boolean isFinished() {
		return finished;
	}
	public boolean shouldMonitor(TrackingContext trackingCtx) {
		return true;// sub class can override this.
	}
	
	private Map<String, Object> storage = new HashMap<String, Object>();
	public void setAttribute(String key, Object value) {
		if(finished)
			throw new IllegalStateException("this life-cycle was already finished, should never be reused.");
		storage.put(key, value);
	}
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		if(finished)
			throw new IllegalStateException("this life-cycle was already finished, should never be reused.");
		return (T) storage.get(key);
	}
	
	public MonitorState getState() {
		return mState;
	}
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private class MonitorStateImpl implements MonitorState{ 
		protected transient MonitorPoint current;
		protected int currentDepth;
		protected int counter;
		
		public MonitorPoint getCurrent() {
			return current;
		}
		public int getCurrentDepth() {
			return currentDepth;
		}
		public int increament() {
			return counter++;
		}
		public int size() {
			return counter;
		}
		public boolean isFinished() {
			return current == null;
		}
		public MonitorPoint start(TrackingContext trackingCtx) {
			MonitorSequence mSquence = init();
			if(mSquence.getRoot()==null){
				current = trackingCtx.create(null);
				mSquence.setRoot(current);
			}else{
				current = trackingCtx.create(current);
			}
			currentDepth++;
			return current;
		}
		public MonitorPoint record(TrackingContext trackingCtx) {
			MonitorPoint rec = trackingCtx.create(current);
			return rec;
		}
		public MonitorPoint end(TrackingContext trackingCtx) {
			if(current==null){
				//TODO how about return null?
				throw new IllegalStateException("you already ended this monitor Sequence and want to end it again?");
			}
			
			MonitorPoint endMP = null;
//			if(trackingCtx!=null){
				if(current.getCreateMillis() > trackingCtx.getCreateMillis()){
					throw new IllegalArgumentException("try to tag a monitor point which create time is smaller than start stack.");
				}
				endMP = trackingCtx.create(current);
//			}
			
			currentDepth--;
			current = current.getParent();
			return endMP;
		}
		
	}//end of class...

}
