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
	private Map<String, Object> storage = new HashMap<String, Object>();
	
	public MonitorSequence getInstance() {
		if(getMonitorSequence() == null){
			setMonitorSequence(new MonitorSequence());
		}
		return getMonitorSequence();
	}
	protected void setMonitorSequence(MonitorSequence mSequence) {
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
	public void flush() {
		ZMonitorManager.getInstance().getMonitorSequenceHandlerRepository().handle(mSquence);
		mSquence = null;
	}
	public boolean shouldMeasure(Name name, String mesg, long createMillis) {
		return true;// override this.
	}

	public void setAttribute(String key, Object value) {
		storage.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) storage.get(key);
	}
}
