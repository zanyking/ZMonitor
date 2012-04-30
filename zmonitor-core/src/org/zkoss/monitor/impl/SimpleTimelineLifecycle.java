/**NDCTimelineLifecycle.java
 * 2011/4/1
 * 
 */
package org.zkoss.monitor.impl;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.spi.TimelineLifecycle;

/**
 * 
 * this implementation will manage the {@link Timeline} instance from {@link ThreadLocal}.
 * {@inheritDoc}<br> 
 */
public class SimpleTimelineLifecycle implements TimelineLifecycle {
	
	protected Timeline timeline;
	private Map<String, Object> storage = new HashMap<String, Object>();
	
	public Timeline getInstance() {
		if(getTimeline() == null){
			setTimeline(new Timeline());
		}
		return getTimeline();
	}
	protected void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	public Timeline getTimeline() {
		return timeline;
	}
	public boolean isInitialized() {
		return timeline!=null;
	}
	public boolean hasTimelineStarted() {
		if(!isInitialized())return false;
		return timeline.isStarted();
	}
	public void reset() {
		ZMonitorManager.getInstance().getTimelineHandlerRepository().handle(timeline);
		timeline = null;
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
