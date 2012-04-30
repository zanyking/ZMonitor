/**ZMonitorManager.java
 * 2011/3/3
 * 
 */
package org.zkoss.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.monitor.impl.DefaultMeasurePointInfoFactory;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.CustomConfiguration;
import org.zkoss.monitor.spi.MeasurePointInfoFactory;
import org.zkoss.monitor.spi.TimelineHandler;
import org.zkoss.monitor.spi.TimelineLifecycle;
import org.zkoss.monitor.spi.TimelineLifecycleManager;


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorManager {

	
	public ZMonitorManager(){}
	
	private String uuid;
	public void setUuid(String id){
		this.uuid = id;
	}
	public String getUuid(){
		return uuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ZMonitorManager getInstance(){
		return Ignitor.getInstance();
	}
	
	@SuppressWarnings("rawtypes")
	private final Map<Class, CustomConfiguration> customConfigurations = 
		new LinkedHashMap<Class, CustomConfiguration>();
	
	@SuppressWarnings("unchecked")
	public <T extends CustomConfiguration> T getCustomConfiguration(Class<T> clazz){
		return (T) customConfigurations.get(clazz);
	}
	public void addCustomConfiguration(CustomConfiguration config){
		customConfigurations.put(config.getClass(), config);
	}
	
	private  MeasurePointInfoFactory measurePointInfoFactory = new DefaultMeasurePointInfoFactory(); 
	public  MeasurePointInfoFactory getMeasurePointInfoFactory() {
		return measurePointInfoFactory;
	}
	public  void setMeasurePointInfoFactory(MeasurePointInfoFactory mpInfoFac) {
		ZMLog.debug("ZMonitorManager::measurePointInfoFactory = "+mpInfoFac);
		measurePointInfoFactory = mpInfoFac;
	}
	

	private final TimelineHandlerRepository timelineHandlerRepository = new TimelineHandlerRepository();
	public TimelineHandlerRepository getTimelineHandlerRepository() {
		return timelineHandlerRepository;
	}
	public void addTimelineHandler(String name, TimelineHandler handler) {
		ZMLog.debug("ZMonitorManager::addTimelineHandler: " +
				name+" = "+handler);
		timelineHandlerRepository.add(name, handler);
	}
	public void removeTimelineHandler(String name){
		timelineHandlerRepository.remove(name);
	}
	
	private static final MPInterceptor NULL_MPInterceptor = new MPInterceptor() {
		public void doBeforeCompose(MPContext mpCtx) {}
	}; 
	private  MPInterceptor mPInterceptor = NULL_MPInterceptor;
	public MPInterceptor getMPInterceptor() {
		return mPInterceptor;
	}
	public void setMPInterceptor(MPInterceptor mPInterceptor) {
		this.mPInterceptor = mPInterceptor;
	}
	
	
	private TimelineLifecycleManager lifecycleManager;
	/**
	 * this is a life-cycle method which will be called while ignition, should not be called in general customization.
	 * @param lifecycleManager
	 */
	public void setLifecycleManager(
			TimelineLifecycleManager lifecycleManager) {
		this.lifecycleManager = lifecycleManager;
	}
	public TimelineLifecycleManager getLifecycleManager() {
		return lifecycleManager;
	}
	public TimelineLifecycle getTimelineLifecycle() {
		return getLifecycleManager().getLifecycle();
	}
	
	private Agent agent;
	public Agent getAgent() {
		return agent;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public void destroy() {//TODO: make sure every Component of ZMonitor has been destroyed.
		this.getTimelineHandlerRepository().destroy();		
	}
	
	
}
