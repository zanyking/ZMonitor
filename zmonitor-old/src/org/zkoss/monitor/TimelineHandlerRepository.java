/**TimelineHandlerRepository.java
 * 2011/4/4
 * 
 */
package org.zkoss.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.TimelineHandler;

/**
 * Used to manage all {@link TimelineHandler} instances in {@link ZMonitorManager}.
 * @author Ian YT Tsai(Zanyking)
 */
public class TimelineHandlerRepository implements TimelineHandler {

	private final Map<String, TimelineHandler> timelineHandlers = 
		Collections.synchronizedMap(new LinkedHashMap<String, TimelineHandler>());
	/**
	 * 
	 * @param register a {@link TimelineHandler}
	 */
	public void add(String name, TimelineHandler handler){
		timelineHandlers.put(name, handler);
	}
	/**
	 * 
	 * @return all registered {@link TimelineHandler} 
	 */
	public List<TimelineHandler> getAll(){
		return new ArrayList<TimelineHandler>(timelineHandlers.values());
	}
	/**
	 * 
	 * @param handlerClass
	 * @return
	 */
	public TimelineHandler get(String handlerClass){
		return timelineHandlers.get(handlerClass);
	}
	/**
	 * 
	 * @param name
	 */
	public void remove(String name) {
		timelineHandlers.remove(name);
	}
	/**
	 *  handle given timeline using every {@link TimelineHandler} registered in this repository.
	 */
	public void handle(Timeline execTLine) {
		for(TimelineHandler tHandler : new ArrayList<TimelineHandler>(timelineHandlers.values())){
			ZMLog.debug("handler:" + tHandler.getClass().getSimpleName()+
					": start to handle Timeline: "+execTLine);
			try{
				tHandler.handle(execTLine);
			}catch(Exception x){
				ZMLog.warn(x,x.getMessage());
			}
		}		
		
	}

	/**
	 * Dispose every {@link TimelineHandler} registered in this repository.
	 */
	public void destroy() {
		for(TimelineHandler tHandler : 
			new ArrayList<TimelineHandler>(timelineHandlers.values())){
			tHandler.destroy();
		}
	}


}
