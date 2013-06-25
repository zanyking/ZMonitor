/**
 * 
 */
package org.zmonitor.test.junit;

import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.zmonitor.util.Loader;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Log4JTestBase extends TestBase {


	protected void beforeZmonitorManagerInit(){
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url = Loader.getResource(packagePath+"/log4j.xml");
		 new DOMConfigurator().doConfigure(url, 
			      LogManager.getLoggerRepository());
	}
	
}
