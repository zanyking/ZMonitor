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
	final MonitoredResult repo =new MonitoredResult();
	public MonitoredResult getMonitoredResult(){
		return repo;
	}
	
	public void handle(MonitorSequence mSequence) {
		System.out.println("InternalTestMonitorSequenceHandler append new ms:"+mSequence);
		getMonitoredResult().add(mSequence);
	}
	
	public InternalTestMonitorSequenceHandler(String id){
		this.setId(id);
	}
}
