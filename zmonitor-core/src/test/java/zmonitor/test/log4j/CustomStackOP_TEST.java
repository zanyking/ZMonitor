/**
 * 
 */
package zmonitor.test.log4j;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.logger.LoggerAdapter;
import org.zmonitor.test.Log4JTestBase;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CustomStackOP_TEST extends Log4JTestBase {

	@Before
	public void doZMonitorConfig(){
		ZMonitorManager zm = ZMonitorManager.getInstance();
		LoggerAdapter la = zm.getBeanIfAny(LoggerAdapter.class);
		
	}
	
	
	@Test
	public void startAndEnd() throws InterruptedException, IOException{
		
		
	}
}
