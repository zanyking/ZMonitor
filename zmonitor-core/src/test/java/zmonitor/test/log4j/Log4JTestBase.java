/**
 * 
 */
package zmonitor.test.log4j;

import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.zmonitor.util.Loader;

import zmonitor.test.TestBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4JTestBase extends TestBase {

	
	@Before
	public void doLog4jConfig(){
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url = Loader.getResource(packagePath+"/log4j.xml");
		 new DOMConfigurator().doConfigure(url, 
			      LogManager.getLoggerRepository());
	}
	
	
	
	
	
	
}
