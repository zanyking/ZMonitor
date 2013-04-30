/**NDCTimelineLifecycle.java
 * 2011/4/1
 * 
 */
package org.zmonitor.impl;

import java.util.HashMap;
import java.util.Map;

import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.Name;
import org.zmonitor.spi.MonitorSequenceLifecycle;

/**
 * 
 * this implementation will manage the {@link MonitorSequence} instance 
 * {@inheritDoc}<br> 
 */
public class SimpleMonitorSequenceLifecycle implements MonitorSequenceLifecycle {
	
	protected MonitorSequence mSquence;
	protected boolean finished;
	
	
	public MonitorSequence getInstance() {
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
		this.mSquence = mSequence;
	}
	public MonitorSequence getMonitorSequence() {
		return mSquence;
	}
	public boolean isInitialized() {
		return mSquence!=null;
	}
	public boolean isMonitorStarted() {
		if(!isInitialized())return false;
		return mSquence.isStarted();
	}
	public void finish() {
		if(finished)
			throw new IllegalStateException("this life-cycle was already finished, should never be reused.");
		ZMonitorManager.getInstance().getMSequenceHandlerRepository().handle(mSquence);
		mSquence = null;
	}
	public boolean isFinished() {
		return false;
	}
	public boolean shouldMeasure(Name name, String mesg, long createMillis) {
		return true;// override this.
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

}
