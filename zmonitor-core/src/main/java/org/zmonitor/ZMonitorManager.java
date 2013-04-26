/**ZMonitorManager.java
 * 2011/3/3
 * 
 */
package org.zmonitor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zmonitor.impl.DefaultMeasurePointInfoFactory;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class ZMonitorManager {

	
	public ZMonitorManager(){}
	

	
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
	
	private  MonitorPointInfoFactory monitorPointInfoFactory = new DefaultMeasurePointInfoFactory(); 
	public  MonitorPointInfoFactory getMeasurePointInfoFactory() {
		return monitorPointInfoFactory;
	}
	public  void setMonitorPointInfoFactory(MonitorPointInfoFactory mpInfoFac) {
		ZMLog.debug("ZMonitorManager::MPInfoFactory = "+mpInfoFac);
		monitorPointInfoFactory = mpInfoFac;
	}
	

	private final MonitorSequenceHandlerRepository monitorSequenceHandlerRepository = 
			new MonitorSequenceHandlerRepository();
	public MonitorSequenceHandlerRepository getMonitorSequenceHandlerRepository() {
		return monitorSequenceHandlerRepository;
	}
	public void addMonitorSequenceHandler(String name, MonitorSequenceHandler handler) {
		ZMLog.debug("ZMonitorManager::addMonitorSequenceHandler: " +
				name+" = "+handler);
		monitorSequenceHandlerRepository.add(name, handler);
	}
	public void removeTimelineHandler(String name){
		monitorSequenceHandlerRepository.remove(name);
	}
	
	
	private MonitorSequenceLifecycleManager lifecycleManager;
	/**
	 * this is a life-cycle method which will be called while ignition, should not be called in general customization.
	 * @param lifecycleManager
	 */
	public void setLifecycleManager(
			MonitorSequenceLifecycleManager lifecycleManager) {
		this.lifecycleManager = lifecycleManager;
	}
	public MonitorSequenceLifecycleManager getLifecycleManager() {
		return lifecycleManager;
	}
	public MonitorSequenceLifecycle getMonitorSequenceLifecycle() {
		return getLifecycleManager().getLifecycle();
	}
	
	public void destroy() {//TODO: make sure every Component of ZMonitor has been destroyed.
		this.getMonitorSequenceHandlerRepository().destroy();		
	}
	
	
}
