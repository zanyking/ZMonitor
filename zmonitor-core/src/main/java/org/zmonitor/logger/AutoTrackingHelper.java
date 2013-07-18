/**
 * 
 */
package org.zmonitor.logger;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.TrackingContext;
import org.zmonitor.ZMonitor;
import org.zmonitor.logger.MessageTupleTracker.MessageTrackingHelper;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycle.MonitorState;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AutoTrackingHelper {
	
	private MessageTrackingHelper mesgHelper;
	/**
	 * 
	 * @param helper 
	 */
	public AutoTrackingHelper(MessageTrackingHelper helper) {
		this.mesgHelper = helper;
	}
	
	private MonitorLifecycle lc;
	private MonitorState mState;
	private LinkedHashMap<String, Pair> stackTraceMap;
	private StackTraceElement[] elements; 
	private void init(TrackingContext tCtx){
		this.lc = tCtx.getLifeCycle();
		this.mState = lc.getState();
		stackTraceMap = new LinkedHashMap<String,Pair>();
		elements = tCtx.getStackTraceElements();
		String key;
//		System.out.println("--------------------------------");
		for(int i=0 ;i<elements.length; i++){
			key = toKey(elements[i]);
//			System.out.println("key["+i+"]: "+key);
			stackTraceMap.put(key, new Pair(key, i));
		}
	}
	/*
	 * 1. get current mp.
	 * 2. retrieve current mp's key(class, method).
	 * 3. if key is null, attache new mp as child of current mp.
	 * 4. if no matches
	 * 
	 */
	public void track(TrackingContext tCtx) {
		init(tCtx);
		
		if(!this.lc.isInitialized()){// this is root.
			ZMonitor.push(tCtx);
			return;
		}
		
		MonitorPoint current = mState.getCurrent();
		MonitorMeta currentMeta;
		String key;
		Pair pair;
		while(current!=null){//find a proper parent for this new mp!
			currentMeta = current.getMonitorMeta();
			if(currentMeta.isCallerNotAvailable()){// cannot be handled automatically, use message
				mesgHelper.track(tCtx);
				return;
			}
			key = toKey(currentMeta);
			
			if((pair = stackTraceMap.get(key))!=null){
				if(pair.idx==0){// new mp is at the same method.
					ZMonitor.record(tCtx);
				}else{// current is 
					ZMonitor.push(tCtx);
				}
				return;
			}
			ZMonitor.pop(lc);// the monitor stack has to be popped without an end MP.
			current = mState.getCurrent();
		}
		
	}
	
	
	
	private static String toKey(StackTraceElement element){
		return element.getClassName()+"::"+element.getMethodName();
	}
	private static String toKey(MonitorMeta currentMeta){
		if(currentMeta.isCallerNotAvailable())
			return null;
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
