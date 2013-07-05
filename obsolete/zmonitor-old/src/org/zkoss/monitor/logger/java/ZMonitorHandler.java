/**ZMonitorHandler.java
 * 2011/11/8
 * 
 */
package org.zkoss.monitor.logger.java;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMonitorHandler extends Handler {

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		// TODO Auto-generated method stub
		record.getLevel();
		record.getMessage();
		record.getMillis();
		record.getThrown();
		record.getSourceClassName();
		record.getSourceMethodName();
		record.getParameters();
		
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub

	}

}
