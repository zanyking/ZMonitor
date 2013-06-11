/**
 * 
 */
package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Dao {

	/**
	 * 
	 */
	public Dao() {
		ZMonitor.record("| Dao constructed.", true);
	}

	public void getBean() {
		ZMonitor.push("> getBean() message with ppp", true);
		lookUpDB();
		ZMonitor.pop("< getBean()", true);
	}

	private void lookUpDB(){
		ZMonitor.push("> lookUpDB() ppp", true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			ZMonitor.pop("< lookUpDB()", true);	
		}
		
	}
}
