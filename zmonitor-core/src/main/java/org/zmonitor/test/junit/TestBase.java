/**TestBase.java
 * 2011/10/24
 * 
 */
package org.zmonitor.test.junit;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.zmonitor.AlreadyStartedException;
import org.zmonitor.InitFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.URLConfigSource;
import org.zmonitor.impl.MSPipe.Mode;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.util.Loader;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class TestBase {
	
	private static final String ID_INTERNAL_TEST_MS_HANDLER = "ID_INTERNAL_TEST_MS_HANDLER";
	public TestBase(){
		this(true);
	}
	public TestBase(boolean useInternalHandler){
	}
	
	protected InternalTestMonitorSequenceHandler newInstance(){
		return new InternalTestMonitorSequenceHandler(ID_INTERNAL_TEST_MS_HANDLER);
	}
	
	private void init() throws Exception{
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url =  findSettingFromPackagePath(packagePath);
		if(url==null){
			throw new InitFailureException("cannot find Configuration:["+
					ConfigSource.ZMONITOR_XML+
					"] from every level of package: [" +packagePath+
					"]. Current application context is: "+this.getClass());
		}
		ZMLog.info("ZMonitor JUnit TestBase: load config from: [",url,"]");
		
		aZMonitorManager.performConfiguration(new URLConfigSource(url));
		Mode mode = aZMonitorManager.getMSPipe().getMode();
		if(Mode.SYNC != mode){
			throw new IllegalStateException(
				"the mode of MSPipe must be synchronized during test!  pipe mode:"+mode); 
		}
		
		ThreadLocalMonitorLifecycleManager lifecycleMgmt = 
			new ThreadLocalMonitorLifecycleManager();
		
		aZMonitorManager.setLifecycleManager(lifecycleMgmt);
		
		aZMonitorManager.addMonitorSequenceHandler(newInstance());
		
		ZMonitorManager.init(aZMonitorManager);
		ZMLog.info(">> Ignit ZMonitor in: ",this.getClass().getCanonicalName());
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if(!ZMonitorManager.isInitialized()){
			init();
		}
		runCase();
	}
	protected void runCase(){
		//override by sub class...
	}
	


	/**
	 * a.b -> a/b/zm.xml
	 * a -> a/zm.xml
	 * 
	 * 
	 * @param packagePath must be "a/b/c", not "a.b.c"
	 */
	private static URL findSettingFromPackagePath(String packagePath){
		URL url = Loader.getResource(packagePath+"/"+ConfigSource.ZMONITOR_XML);
		if(url!=null)return url;
		
		int lastIdx = packagePath.lastIndexOf(".");
		if(lastIdx<=0)
			return Loader.getResource(ConfigSource.ZMONITOR_XML);
		
		String parent = packagePath.substring(0, lastIdx);
		
		return (url==null)? findSettingFromPackagePath(parent) :
			url;
	}

	protected InternalTestMonitorSequenceHandler getInternalMSHandler(){
		return ZMonitorManager.getInstance().getBeanById(ID_INTERNAL_TEST_MS_HANDLER);
	}
	
	
	/*========================================= */
	
	public MonitoredResult getMonitoredResult(){
		if(getInternalMSHandler()==null){
			throw new IllegalStateException( 
				"this test case didn't activate internalMSHandler, class:"+this.getClass());
		}
		return getInternalMSHandler().getMonitoredResult();
	}
	
	
}
