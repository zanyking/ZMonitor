/**ZMonitorManager.java
 * 2011/3/3
 * 
 */
package org.zmonitor;

import java.util.Collections;
import java.util.List;

import org.zmonitor.bean.ZMBean;
import org.zmonitor.bean.ZMBeanRepository;
import org.zmonitor.bean.ZMBeanRepositoryBase;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.impl.ConfiguratorRepository;
import org.zmonitor.impl.MSPipe;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.marker.BasicMarkerFactory;
import org.zmonitor.marker.IMarkerFactory;
import org.zmonitor.selector.SelectorAdaptor;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.spi.MonitorLifecycleManager;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.Arguments;


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public final class ZMonitorManager {
	
	private static final ZMonitorManager NOOP = 
		new ZMonitorManager(new NOOPZMBeanRepository(), 
				new NOOPMonitorLifecycleManager());
	
	//class-Loader singleton.
	private static volatile ZMonitorManager sZMM = NOOP;
	
	/**
	 * without ignition, the default instance is a NULL object with no-operation implementation.
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
		ZMLog.info("ZMonitor Ignition START... ");// will print out in any case, since there's no customized ZKLog here yet!
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
	private MSPipe msPipe;
	public void setMSPipe(MSPipe pipe) {
		msPipe = pipe;
		fZMBeanRepository.add(msPipe);
	}
	
	public MSPipe getMSPipe(){
		return msPipe;
	}
	
	
	public ZMonitorManager(){
		fZMBeanRepository = new ZMBeanRepositoryBase(){};
	}
	
	private ZMonitorManager(ZMBeanRepository beanRepo,
			NOOPMonitorLifecycleManager lifecycleManager){
		fZMBeanRepository = beanRepo;
		this.lifecycleManager = lifecycleManager;
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
	
	private final ConfiguratorRepository cRepo = new ConfiguratorRepository();
	private ConfigSource configSource;
	/**
	 * 
	 * @return
	 */
	public void performConfiguration(ConfigSource configSource){
		Arguments.checkNotNull(configSource, "configSource cannot be null!");
		this.configSource = configSource;
		cRepo.scan();
		cRepo.performConfiguration(ZMonitorManager.this);
	}
	public ConfigSource getConfigSource() {
		return configSource;
	}
	
	private ResourceProvider resourceProvider;

	/**
	 * 
	 * @return
	 */
	public ResourceProvider getResourceProvider() {
		return resourceProvider;
	}
	/**
	 * 
	 * @param resourceProvider if the given object is also a ZMBean, 
	 * the life-cycle will be managed by ZMonitorManager.
	 */
	public void setResourceProvider(ResourceProvider resourceProvider) {
		if(resourceProvider==null)return;
		if(this.resourceProvider!=null)
			throw new IllegalStateException("a ResourceProvider is already exist: "+
					this.resourceProvider.getClass());
		
		this.resourceProvider = resourceProvider;
		this.accept(resourceProvider);
	}
	IMarkerFactory markerFactory = new BasicMarkerFactory();
	public IMarkerFactory getMarkerFactory(){
		return markerFactory;
	}
	
	public void setMarkerFactory(IMarkerFactory markerFac){
		if (fZMBeanRepository.isStarted())
			throw new IllegalStateException(
					"marker factory can only be initiated while " +
					"ZMonitorManager is in configuration phase.");
		markerFactory = markerFac;
	}
	
	
	/**
	 * @param tClz bean type.
	 * @return every bean that matches the given type, size could be 0~n.
	 */
	public <T> List<T> getBeans(Class<T> tClz) {
		return fZMBeanRepository.get(tClz);
	}
	/**
	 * get bean by type.
	 * @param tClz
	 * @return the bean if single, the first element if multiple, null if empty. 
	 */
	public <T> T getBeanIfAny(Class<T> tClz){
		List<T> li = getBeans(tClz);
		if(li.isEmpty())return  null;
		return li.get(0);
	}
	/**
	 * 
	 * @param id getBean by id.
	 * @return
	 */
	public <T> T getBeanById(String id){
		return fZMBeanRepository.get(id);
	}
	/**
	 * register a bean if it is a ZMBean, otherwise do nothing. 
	 * @param bean
	 */
	public void accept(Object bean){
		if(bean instanceof ZMBean){
			register((ZMBean) bean);	
		}
	}
	/**
	 * 
	 * @param bean
	 */
	public void register(ZMBean bean){
		fZMBeanRepository.add(bean);
	}
	
	/**
	 * 
	 * @param handler
	 */
	public void addMonitorSequenceHandler( MonitorSequenceHandler handler) {
		ZMLog.debug("ZMonitorManager::addMonitorSequenceHandler: " +
				handler.getId()+" = "+handler);
		fZMBeanRepository.add(handler);
	}
	/**
	 * 
	 * @param name
	 */
	public void removeMonitorSequenceHandler(String name){
		fZMBeanRepository.remove(name);
	}
	/**
	 * 
	 * @param mSquence
	 */
	public void handle(MonitorSequence mSquence) {
		getMSPipe().pipe(mSquence);
	}
	
	
	private MonitorLifecycleManager lifecycleManager;
	/**
	 * this is a life-cycle method which will be called while ignition, should not be called in general customization.
	 * @param lifecycleManager
	 */
	public void setLifecycleManager(
			MonitorLifecycleManager lifecycleManager) {
		this.lifecycleManager = lifecycleManager;
	}
	public MonitorLifecycleManager getLifecycleManager() {
		return lifecycleManager;
	}
	public MonitorLifecycle getMonitorLifecycle() {
		return getLifecycleManager().getLifecycle();
	}

	/**
	 * tell ZMonitor how to adapt a MonitorPoint instance to selector engine.  
	 */
	private SelectorAdaptor selectorAdaptor = new SelectorAdaptor(); 
	/**
	 * 
	 * @return
	 */
	public SelectorAdaptor getSelectorAdaptor(){
		return selectorAdaptor;
	}
	/**
	 * 
	 * @param selectorAdaptor
	 */
	public void setSelectorAdaptor(SelectorAdaptor selectorAdaptor){
		this.selectorAdaptor = selectorAdaptor;
	}
}//end of class
/**
 * an noop implementation
 * @author Ian YT Tsai(Zanyking)
 *
 */
class NOOPZMBeanRepository implements ZMBeanRepository{

	public <T> List<T> get(Class<T> clz) {return Collections.emptyList();}
	public <T> T get(String id) {return null;}
	public void add(ZMBean zmBean) {}
	public void remove(String id) {}
	public void remove(ZMBean zmBean) {}
	public boolean isStarted() {return false;}
	public boolean isStopped() {return false;}
	public void start() {}
	public void stop() {}
	public String getId() {
		return "noop";
	}
	public void setId(String id) {
		
	}
}//end of class
/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
class NoOpMonitorState implements MonitorLifecycle.MonitorStack{

	public MonitorPoint getCurrent() {
		return null;
	}

	public int getCurrentDepth() {
		return 0;
	}

	public int increament() {
		return 0;
	}

	public int size() {
		return 0;
	}

	public boolean isEmpty() {
		return false;
	}

	public MonitorPoint push(TrackingContext trackingCtx) {
		return null;
	}

	public MonitorPoint pinpoint(TrackingContext trackingCtx) {
		return null;
	}

	public MonitorPoint pop(TrackingContext trackingCtx) {
		return null;
	}
	
}
/**
 * @author Ian YT Tsai(Zanyking)
 */
class NOOPMonitorLifecycleManager implements MonitorLifecycleManager{
	private static final MonitorLifecycle noop = new MonitorLifecycle(){
		private final NoOpMonitorState NOOP_STATE = new NoOpMonitorState();
		public boolean shouldMonitor(TrackingContext trackingCtx) {
			return false;// this should take care the rest of all...
		}
		public MonitorSequence init() {
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
		}
		public MonitorStack getStack() {
			return NOOP_STATE;
		}
		};
	public MonitorLifecycle getLifecycle() {
		return noop;
	}
	public void disposeLifecycle(MonitorLifecycle lfc) {
		//nothing todo with noop lfc...
	}
}//end of class
