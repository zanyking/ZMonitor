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
	/**
	 * 
	 * @param tCtx
	 */
	public AutoTrackingHelper(TrackingContext tCtx) {
		this.tCtx = tCtx;
		this.lc = tCtx.getLifeCycle();
		this.mState = lc.getState();
	}


	public void track() {
		MonitorPoint current = mState.getCurrent();
		if(current==null){// this is root.
			ZMonitor.push(tCtx);
			return;
		}
		MonitorMeta currentMeta;
		LinkedHashSet<String> set = toSet(tCtx.getStackTraceElements());
		int popCounter = 0;
		
		while(true){
			currentMeta = current.getMonitorMeta();
			StackTraceElement[] elements = currentMeta.getStackTraceElements();
			
			current = mState.getCurrent();				
		}
	}
	
	
	private static LinkedHashSet<String> toSet(StackTraceElement[] elements){
		LinkedHashSet<String> ans = new LinkedHashSet<String>();
		for(StackTraceElement element : elements){
			ans.add(toKey(element));
		}
		return ans;
	}
	
	private static String toKey(StackTraceElement element){
		return element.getClassName()+"::"+element.getFileName();
	}
	private static String toKey(MonitorMeta currentMeta){
		return currentMeta.getClassName()+"::"+currentMeta.getMethodName();
	}
}
