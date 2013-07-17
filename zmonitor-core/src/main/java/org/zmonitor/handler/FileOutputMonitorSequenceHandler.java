/**
 * 
 */
package org.zmonitor.handler;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.MonitorSequenceHandler;

/**
 * @author ian.tsai
 *
 */
public class FileOutputMonitorSequenceHandler extends ZMBeanBase 
implements MonitorSequenceHandler, CustomConfigurable {

	public void configure(ConfigContext configCtx) {
		
	}

	public void handle(MonitorSequence mSequence) {
		
	}

}
