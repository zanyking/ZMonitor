/**PureJavaDemo2.java
 * 2011/4/4
 * 
 */
package demo;

import org.zkoss.monitor.ZMonitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class PureJavaDemo2 {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception{
		ZMonitor.push("this is a test mesg!", true);
		ZMonitor.push("second stack", true);
		ZMonitor.pop(true);
		ZMonitor.record("");
		method1();
		ZMonitor.pop(true);
	}
	
	public static void method1(){
		ZMonitor.push("start method 1", true);
		ZMonitor.record("point 1");
		ZMonitor.record("point 2");
		ZMonitor.pop(true);
		
		
		ZMonitor.push("new Timeline", true);
		ZMonitor.pop(true);
	}

}

class Test{
	static {
		System.out.println("static init Test!!!");
	}
}
