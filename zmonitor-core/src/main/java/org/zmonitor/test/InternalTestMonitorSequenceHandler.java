/**
 * 
 */
package org.zmonitor.test;

import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.spi.MonitorSequenceHandler;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class InternalTestMonitorSequenceHandler extends ZMBeanBase 
implements MonitorSequenceHandler {
	private static final ThreadLocal<MonitorResult> RESULT_REF = 
			new InheritableThreadLocal<MonitorResult>();
	
	public MonitorResult getResult(){
		MonitorResult repo = RESULT_REF.get();
		if(repo==null){
			RESULT_REF.set(repo = new MonitorResult());
		}
		return repo;
	}
	
	public void handle(MonitorSequence mSequence) {
		getResult().add(mSequence);
	}

	public void clearThreadLocal(){
		RESULT_REF.remove();
	}
	
}
