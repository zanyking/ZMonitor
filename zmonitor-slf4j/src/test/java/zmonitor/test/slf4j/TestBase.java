/**TestBase.java
 * 2011/10/24
 * 
 */
package zmonitor.test.slf4j;

import java.net.URL;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.zmonitor.IgnitionFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.URLConfigSource;
import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.util.Loader;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestBase {
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if(ZMonitorManager.isInitialized())return;
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url =  findSettingFromPackagePath(packagePath);
		if(url==null){
			throw new IgnitionFailureException("cannot find Configuration:["+
					ConfigSource.ZMONITOR_XML+
					"] from every level of package: [" +packagePath+
					"]. Current application context is: "+this.getClass());
		}
		ZMLog.info("ZMonitor JUnit TestBase: load config from: [",url,"]");
		
		aZMonitorManager.performConfiguration(new URLConfigSource(url));
		
		ThreadLocalMonitorSequenceLifecycleManager lifecycleMgmt = 
			new ThreadLocalMonitorSequenceLifecycleManager();
		
		aZMonitorManager.setLifecycleManager(lifecycleMgmt);
		
		ZMonitorManager.init(aZMonitorManager);
		ZMLog.info(">> Ignit ZMonitor in: ",this.getClass().getCanonicalName());	
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
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		//Ignitor.destroy();
	}
}
