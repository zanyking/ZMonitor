package zmonitor.test.clz;

import org.zmonitor.ZMonitor;

public class BusinessObject {
	Service b;
	
	public BusinessObject() {
		ZMonitor.push(">> constructing BusinessObject...", true);
		b = new Service(new Dao());
		ZMonitor.pop("<< BusinessObject constructed.", true);
	}

	
	public void doBiz(){
		ZMonitor.push(">> doBiz()", true);
		b.doService();
		ZMonitor.pop("<< doBiz()", true);
	}
}
