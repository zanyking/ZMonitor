/**
 * 
 */
package org.zmonitor.logger;

import java.util.LinkedHashSet;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycle.MonitorState;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AutoTrackingHelper {
	private final TrackingContext tCtx;
	private MonitorLifecycle lc;
	private MonitorState mState;
	private LinkedHashSet<Pair> stackTraceSet;
	
	/**
	 * 
	 * @param tCtx
	 */
	public AutoTrackingHelper(TrackingContext tCtx) {
		this.tCtx = tCtx;
		this.lc = tCtx.getLifeCycle();
		this.mState = lc.getState();
		stackTraceSet = new LinkedHashSet<Pair>();
		StackTraceElement[] elements = tCtx.getStackTraceElements();
		for(int i=0 ;i<elements.length; i++){
			stackTraceSet.add(new Pair(toKey(elements[i]), i));
		}
	}

	/*
	 * 1. get current mp.
	 * 2. retrieve current mp's key(class, method).
	 * 3. if key is null, attache new mp as child of current mp.
	 * 4. if no matches
	 * 
	 */
	public void track() {
		MonitorPoint current = mState.getCurrent();
		
		if(current==null){// this is root.
			ZMonitor.push(tCtx);
			return;
		}
		MonitorPoint parent = current.getParent();
		if(parent==null){// current is root, must be it's child.
			
		}
		MonitorMeta currentMeta;
		
		int popCounter = 0;
		
		while(true){
			currentMeta = current.getMonitorMeta();
			StackTraceElement[] elements = currentMeta.getStackTraceElements();
			
			current = mState.getCurrent();				
		}
	}
	
	
	
	private static String toKey(StackTraceElement element){
		return element.getClassName()+"::"+element.getFileName();
	}
	private static String toKey(MonitorMeta currentMeta){
		return currentMeta.getClassName()+"::"+currentMeta.getMethodName();
	}
	
	private class Pair{
		final String key;
		final int idx;
		public Pair(String key, int idx) {
			super();
			this.key = key;
			this.idx = idx;
		}
	}
}
