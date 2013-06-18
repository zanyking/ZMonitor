/**
 * 
 */
package org.zmonitor.test.junit;

import java.util.concurrent.ConcurrentHashMap;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.Arguments;

/**
 * 
 * Temporary storing MonitorSequences   
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
class InternalTestMonitorSequenceHandler extends ZMBeanBase 
implements MonitorSequenceHandler {
	
	private final ConcurrentHashMap<Class<? extends TestBase>, MonitoredResult> mrStore = 
			new ConcurrentHashMap<Class<? extends TestBase>, MonitoredResult>();
	
	/**
	 * 
	 * @param class1
	 * @return
	 */
	public MonitoredResult getMonitoredResult(
			Class<? extends TestBase> class1){
		
		Arguments.checkNotNull(class1);
		MonitoredResult result = mrStore.get(class1);
		if(result == null){
			mrStore.put(class1, 
				result = new MonitoredResult(class1)); 
		}
		return result;
	}
	
	public void handle(MonitorSequence mSequence) {
		MonitorMeta meta = mSequence.getRoot().getMonitorMeta();
		Class clz;
		try {
			clz = Class.forName(meta.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new NotAbleToGetTestCaseClassException(meta, e);
		}
		
		getMonitoredResult(clz).add(mSequence);
	}
	public InternalTestMonitorSequenceHandler(String id){
		Arguments.checkNotEmpty(id);
		this.setId(id);
	}
	
}//end of class...
/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
class NotAbleToGetTestCaseClassException extends RuntimeException{
	private static final long serialVersionUID = -7035476462683321875L;

	public NotAbleToGetTestCaseClassException(MonitorMeta meta,  Throwable cause) {
		super("not able to retrieve class info from meta:"+meta, cause);
	}

	public NotAbleToGetTestCaseClassException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotAbleToGetTestCaseClassException(String message) {
		super(message);
	}

	public NotAbleToGetTestCaseClassException(Throwable cause) {
		super(cause);
	}
	
}
