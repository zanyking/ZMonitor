/**ZMonitorManager.java
 * 2011/3/3
 * 
 */
package org.zmonitor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zmonitor.bean.ZMBean;
import org.zmonitor.bean.ZMBeanRepository;
import org.zmonitor.bean.ZMBeanRepositoryAbstract;
import org.zmonitor.impl.DefaultMeasurePointInfoFactory;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.MonitorSequenceLifecycleManager;
import org.zmonitor.spi.Name;
import org.zmonitor.spi.XMLConfiguration;


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public final class ZMonitorManager {
	
	private static final ZMonitorManager NOOP = 
		new ZMonitorManager(new NoOpZMBeanRepository());
	static{
		NOOP.setLifecycleManager(new MonitorSequenceLifecycleManager(){
			MonitorSequenceLifecycle noop = new MonitorSequenceLifecycle(){

				public boolean shouldMeasure(Name name, String mesg,
						long createMillis) {
					return false;// this should take care the rest of all...
				}
				public MonitorSequence getInstance() {
					return null;
				}
				public MonitorSequence getMonitorSequence() {
					return null;
				}
				public boolean isInitialized() {
					return false;
				}
				public boolean isMonitorStarted() {
					return false;
				}
				public void finish() {
				}
				public boolean isFinished() {
					return false;
				}
				public void setAttribute(String key, Object value) {
				}
				public <T> T getAttribute(String key) {
					return null;
				}};
			public MonitorSequenceLifecycle getLifecycle() {
				return noop;
			}});
	}
	//class-Loader singleton.
	private static volatile ZMonitorManager sZMM = NOOP;
	
	/**
	 * without ignition, the default instance is a NULL object with noop implementaiton.
	 * @return the current ZMonitorManager instance.
	 * 
	 */
	public static ZMonitorManager getInstance(){
		return sZMM;
	}
	/**
	 * 
	 * @return true, if a proper ZMonitorManager is initialized, false otherwise.
	 */
	public static synchronized boolean isInitialized(){
		return sZMM != NOOP && sZMM.isStarted();
	}
	
	/**
	 * ignite a ZmonitorManager, 
	 * @param manager will be used by 
	 * @return true, if successfully initialized, false, there's already a started ZMonitorManager.  
	 * @throws AlreadyStartedException 
	 */
	public static synchronized void init(ZMonitorManager manager) throws AlreadyStartedException{
		if(manager==NOOP)return;
		
		if(manager.isStopped()){
			throw new IllegalArgumentException("the given instance is already stopped");
		}
		if(sZMM.isStarted()){
			throw new AlreadyStartedException();
		}
		if(!manager.isStarted()){
			manager.start();
		}
		sZMM = manager; 
	}
	/**
	 * 
	 */
	public static synchronized void dispose(){
		if(sZMM==NOOP)return;
		ZMonitorManager m = sZMM;
		sZMM = NOOP;
		m.stop();
	}
	
	private final ZMBeanRepository aZMBeanRepository;
	
	public ZMonitorManager(){
		aZMBeanRepository = new ZMBeanRepositoryAbstract(){
			protected void doStart() {
				ZMLog.info("ZMonitor Ignition START... ");// will print out in any case, since there's no customized ZKLog here yet!
					
				if(configSource != null){
					//TODO: call Configurator at this part...
					doXMLConfiguration(configSource);
				}
				
				try {//TODO log4j shouldn't pollute this part of code...
					Class.forName("org.zmonitor.logger.log4j.Driver");
				} catch (Throwable e) {
					ZMLog.info("log4j is not applicable in this environment: " +
							e.getClass()+" : "+e.getMessage());
				}
			}
			protected void doStop() {//TODO: make sure every zmbean of ZMonitor has been destroyed.
				getMSequenceHandlerRepository().destroy();	
			}
			
		};
	}

	private void doXMLConfiguration(ConfigSource configSource){
		XMLConfiguration ctxt = null;
		//TODO: construct a proper ConfigContext
		//1. Retrieve configurators from each zmonitor-xxx.jar .
		//2. Initialize configurator properly.
		//3. Call configure() of each configurator.
		
	}
	
	private ZMonitorManager(ZMBeanRepository a){
		aZMBeanRepository = a;
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
	

	private final MonitorSequenceHandlerRepository msHandlerRepo = 
			new MonitorSequenceHandlerRepository();
	public MonitorSequenceHandlerRepository getMSequenceHandlerRepository() {
		return msHandlerRepo;
	}
	public void addMonitorSequenceHandler(String name, MonitorSequenceHandler handler) {
		ZMLog.debug("ZMonitorManager::addMonitorSequenceHandler: " +
				name+" = "+handler);
		msHandlerRepo.add(name, handler);
	}
	public void removeMonitorSequenceHandler(String name){
		msHandlerRepo.remove(name);
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

	
	private ConfigSource configSource;
	public ConfigSource getConfigSource() {
		return configSource;
	}
	public void setConfigSource(ConfigSource configSource) {
		this.configSource = configSource;
	}


	public boolean isStarted() {
		return aZMBeanRepository.isStarted();
	}
	public boolean isStopped() {
		return aZMBeanRepository.isStopped();
	}
	public void start() {
		aZMBeanRepository.start();
	}
	public void stop() {
		aZMBeanRepository.stop();
	}
}//end of class
/**
 * an noop implementation
 * @author Ian YT Tsai(Zanyking)
 *
 */
class NoOpZMBeanRepository implements ZMBeanRepository{

	public <T> Collection<T> get(Class<T> clz) {return Collections.EMPTY_LIST;}
	public <T> T get(String id) {return null;}
	public void add(ZMBean zmBean) {}
	public void remove(String id) {}
	public void remove(ZMBean zmBean) {}
	public boolean isStarted() {return false;}
	public boolean isStopped() {return false;}
	public void start() {}
	public void stop() {}
}

