package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

public class A {
	B b;
	
	public A() {
		ZMonitor.push(">> constructing A...", true);
		b = new B(new C());
		ZMonitor.pop("<< A constructed.", true);
	}

	
	public void doA1(){
		ZMonitor.push(">> doA1()", true);
		b.doB1();
		ZMonitor.pop("<< doA1()", true);
	}
}
