/**
 * 
 */
package org.zmonitor.test.junit;

import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.spi.MonitorSequenceHandler;

/**
 * 
 * Temporary storing MonitorSequences   
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
class InternalTestMonitorSequenceHandler extends ZMBeanBase 
implements MonitorSequenceHandler {
	private static final ThreadLocal<MonitoredResult> RESULT_REF = 
			new InheritableThreadLocal<MonitoredResult>();
	
	public MonitoredResult getMonitoredResult(){
		MonitoredResult repo = RESULT_REF.get();
		if(repo==null){
			RESULT_REF.set(repo = new MonitoredResult());
		}
		return repo;
	}
	
	public void handle(MonitorSequence mSequence) {
		getMonitoredResult().add(mSequence);
	}

	public void clear(){
		RESULT_REF.remove();
	}
	
}
