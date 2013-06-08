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
		ZMonitor.record("| doC1()", true);
	}

}
