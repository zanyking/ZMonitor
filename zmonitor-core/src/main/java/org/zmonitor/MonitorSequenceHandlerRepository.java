/**TimelineHandlerRepository.java
 * 2011/4/4
 * 
 */
package org.zmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.MonitorSequenceHandler;

/**
 * An aggregation class to aggregate all {@link MonitorSequenceHandler} instances of {@link ZMonitorManager}.
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class MonitorSequenceHandlerRepository implements MonitorSequenceHandler {

	private final Map<String, MonitorSequenceHandler> timelineHandlers = 
		Collections.synchronizedMap(new LinkedHashMap<String, MonitorSequenceHandler>());
	/**
	 * 
	 * @param register a {@link MonitorSequenceHandler}
	 */
	public void add(String name, MonitorSequenceHandler handler){
		timelineHandlers.put(name, handler);
	}
	/**
	 * 
	 * @return all registered {@link MonitorSequenceHandler} 
	 */
	public List<MonitorSequenceHandler> getAll(){
		return new ArrayList<MonitorSequenceHandler>(timelineHandlers.values());
	}
	/**
	 * 
	 * @param handlerClass
	 * @return
	 */
	public MonitorSequenceHandler get(String handlerClass){
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
	 *  handle given timeline using every {@link MonitorSequenceHandler} registered in this repository.
	 */
	public void handle(MonitorSequence execTLine) {
		for(MonitorSequenceHandler tHandler : new ArrayList<MonitorSequenceHandler>(timelineHandlers.values())){
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
	 * Dispose every {@link MonitorSequenceHandler} registered in this repository.
	 */
	public void destroy() {
		for(MonitorSequenceHandler tHandler : 
			new ArrayList<MonitorSequenceHandler>(timelineHandlers.values())){
			tHandler.destroy();
		}
	}


}
