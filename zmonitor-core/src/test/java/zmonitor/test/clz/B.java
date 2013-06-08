/**
 * 
 */
package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class B {

	C c;
	
	
	
	public B(C c) {
		ZMonitor.push(">> constructing B...", true);
		this.c = c;
		ZMonitor.pop("<< B constructed.", true);
	}



	public void doB1() {
		ZMonitor.push(">> doB1()", true);
		c.doC1();
		ZMonitor.pop("<< doB1()", true);
	}

}
