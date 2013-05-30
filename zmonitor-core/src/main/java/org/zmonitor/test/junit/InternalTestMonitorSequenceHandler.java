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
	
	public MonitoredResult getThreadLocalRepo(){
		MonitoredResult repo = RESULT_REF.get();
		if(repo==null){
			RESULT_REF.set(repo = new MonitoredResult());
		}
		return repo;
	}
	
	public void handle(MonitorSequence mSequence) {
		getThreadLocalRepo().add(mSequence);
	}

	public void clearThreadLocal(){
		RESULT_REF.remove();
	}
	
}
