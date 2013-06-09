/**
 * 
 */
package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Service {

	Dao c;
	
	
	
	public Service(Dao c) {
		ZMonitor.push(">> constructing Service...", true);
		this.c = c;
		ZMonitor.pop("<< Service constructed.", true);
	}



	public void doService() {
		ZMonitor.push(">> doService()", true);
		c.getBean();
		ZMonitor.pop("<< doService()", true);
	}

}
