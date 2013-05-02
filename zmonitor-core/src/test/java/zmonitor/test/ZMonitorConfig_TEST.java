/**Log4jNdc_TEST.java
 * 2011/10/21
 * 
 */
package zmonitor.test;


import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zmonitor.AlreadyStartedException;
import org.zmonitor.IgnitionFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */

public class ZMonitorConfig_TEST {

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
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void initZMonitorByGivenClassPath() throws InterruptedException, IOException{
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		
		String path = packagePath+"/"+ConfigSource.ZMONITOR_XML;
		
		final ConfigSource configSrc = 
				ConfigSources.loadFromClassPath(path);
			
			if(configSrc==null){
				throw new IgnitionFailureException("cannot find Configuration:["+
						ConfigSource.ZMONITOR_XML+
						"] from current application context: "+ZMonitorConfig_TEST.class);
			}
			ZMonitorManager aZMonitorManager = new ZMonitorManager();
			try {
				ZMonitorManager.init(aZMonitorManager);
			} catch (AlreadyStartedException e) {
				throw new RuntimeException(e);
			}
			ZMonitorManager.dispose();
	}
	
	
	
	
}
