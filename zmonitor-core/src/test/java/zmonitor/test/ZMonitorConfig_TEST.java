/**Log4jNdc_TEST.java
 * 2011/10/21
 * 
 */
package zmonitor.test;


import java.io.IOException;
import java.net.URL;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zmonitor.AlreadyStartedException;
import org.zmonitor.InitFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.config.URLConfigSource;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.test.junit.TestBaseUtils;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */

public class ZMonitorConfig_TEST {


	@Test
	public void initZMonitorByGivenClassPath() throws InterruptedException, IOException{
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		String path = packagePath+"/"+ConfigSource.ZMONITOR_XML;
		
		final ConfigSource configSrc = 
				ConfigSources.loadFromClassPath(path);
			
			if(configSrc==null){
				throw new InitFailureException("cannot find Configuration:["+
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
	@Test
	public void testZMLogDebug() throws InterruptedException, IOException, AlreadyStartedException{
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		
		URL url = TestBaseUtils.findSetting(packagePath, "zmonitor.debug.xml");
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		aZMonitorManager.performConfiguration(new URLConfigSource(url));
		ZMonitorManager.init(aZMonitorManager); 
		Assert.assertEquals(true, ZMLog.isDebug());
		
		ZMonitorManager.dispose();
	}
	
	
	
}
