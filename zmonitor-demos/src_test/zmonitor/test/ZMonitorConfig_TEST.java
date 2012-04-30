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
import org.zkoss.monitor.IgnitionFailureException;
import org.zkoss.monitor.Ignitor;
import org.zkoss.monitor.ZMonitor;
import org.zkoss.monitor.impl.XMLConfigurator;
import org.zkoss.monitor.impl.XmlConfiguratorLoader;


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
		
		String path = packagePath+"/"+XmlConfiguratorLoader.ZMONITOR_XML;
		
		final XMLConfigurator xmlCofig = 
				XmlConfiguratorLoader.loadFromClassPath(path);
			
			if(xmlCofig==null){
				throw new IgnitionFailureException("cannot find Configuration:["+
						XmlConfiguratorLoader.ZMONITOR_XML+
						"] from current application context: "+ZMonitorConfig_TEST.class);
			}
			Ignitor.ignite( xmlCofig);
	}
	
	
	
	
}
