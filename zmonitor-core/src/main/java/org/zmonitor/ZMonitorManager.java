/**ZMonitorManager.java
 * 2011/3/3
 * 
 */
package org.zmonitor;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.zmonitor.bean.ZMBean;
import org.zmonitor.bean.ZMBeanRepository;
import org.zmonitor.bean.ZMBeanRepositoryBase;
import org.zmonitor.impl.ConfiguratorRepository;
import org.zmonitor.impl.DefaultMeasurePointInfoFactory;
import org.zmonitor.impl.XMLConfigurationImpl;
import org.zmonitor.impl.ZMLog;
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
	 * @throws IllegalArgumentException if the given manager is stopped.
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
		// prevent any new Monitor Sequence Operation while stopping...
		m.stop();
	}
	
	private final ZMBeanRepository fZMBeanRepository;
	
	public ZMonitorManager(){
		fZMBeanRepository = new ZMBeanRepositoryBase(){
			protected void doStart() {
				super.doStart();
				ZMLog.info("ZMonitor Ignition START... ");// will print out in any case, since there's no customized ZKLog here yet!
					
				if(configSource != null){
					doXMLConfiguration(configSource);
				}
				
				try {//TODO log4j shouldn't pollute this part of code...
					Class.forName("org.zmonitor.logger.log4j.Driver");
				} catch (Throwable e) {
					ZMLog.info("log4j is not applicable in this environment: " +
							e.getClass()+" : "+e.getMessage());
				}
			}
			
		};
	}

	private void doXMLConfiguration(ConfigSource configSource){
		XMLConfiguration conf;
		try {
			 conf = new XMLConfigurationImpl( 
					configSource.getDOMRetriever());
		} catch (IOException e) {
			throw new IgnitionFailureException(e);
		}
		
		ConfiguratorRepository cRepo = new ConfiguratorRepository();
		cRepo.scan();
		cRepo.performConfiguration(this, conf);
	}
	
	private ZMonitorManager(ZMBeanRepository a){
		fZMBeanRepository = a;
	}
	
	//TODO got a feeling that this might cause a lot of trouble...
//	@SuppressWarnings("rawtypes")
//	private final Map<Class, CustomConfiguration> customConfigurations = 
//		new LinkedHashMap<Class, CustomConfiguration>();
//	
//	@SuppressWarnings("unchecked")
//	public <T extends CustomConfiguration> T getCustomConfiguration(Class<T> clazz){
//		return (T) customConfigurations.get(clazz);
//	}
//	public void addCustomConfiguration(CustomConfiguration config){
//		customConfigurations.put(config.getClass(), config);
//	}
	
	
	private  MonitorPointInfoFactory monitorPointInfoFactory = new DefaultMeasurePointInfoFactory(); 
	public  MonitorPointInfoFactory getMeasurePointInfoFactory() {
		return monitorPointInfoFactory;
	}
	public  void setMonitorPointInfoFactory(MonitorPointInfoFactory mpInfoFac) {
		ZMLog.debug("ZMonitorManager::MPInfoFactory = "+mpInfoFac);
		monitorPointInfoFactory = mpInfoFac;
	}
	

	public void addMonitorSequenceHandler( MonitorSequenceHandler handler) {
		ZMLog.debug("ZMonitorManager::addMonitorSequenceHandler: " +
				handler.getId()+" = "+handler);
		fZMBeanRepository.add(handler);
	}
	public void removeMonitorSequenceHandler(String name){
		fZMBeanRepository.remove(name);
	}
	public void handle(MonitorSequence mSquence) {
		Collection<MonitorSequenceHandler> handlers = 
			fZMBeanRepository.get(MonitorSequenceHandler.class);
		
		//TODO: sync or Async? that's the problem.
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
		return fZMBeanRepository.isStarted();
	}
	public boolean isStopped() {
		return fZMBeanRepository.isStopped();
	}
	public void start() {
		fZMBeanRepository.start();
	}
	public void stop() {
		fZMBeanRepository.stop();
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

