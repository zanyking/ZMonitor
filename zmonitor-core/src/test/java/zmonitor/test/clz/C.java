/**
 * 
 */
package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class C {

	/**
	 * 
	 */
	public C() {
		ZMonitor.record("| C constructed.", true);
	}

	public void doC1() {
		ZMonitor.push("> doC1()", true);
		longPeriod();
		ZMonitor.pop("< message with ppp", true);
	}

	private void longPeriod(){
		ZMonitor.push("> longPeriod() ppp", true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			ZMonitor.pop("< longPeriod()", true);	
		}
		
	}
}
