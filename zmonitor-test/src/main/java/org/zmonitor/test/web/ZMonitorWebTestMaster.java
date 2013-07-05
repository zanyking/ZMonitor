/**
 * 
 */
package org.zmonitor.test.web;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public final class ZMonitorWebTestMaster {
	private ZMonitorWebTestMaster(){}
	private static final ZMonitorWebTestMaster SINGLETON = new ZMonitorWebTestMaster();
	/**
	 * 
	 * @return
	 */
	public static ZMonitorWebTestMaster getInstance() {
		return SINGLETON;
	}
	/**
	 * 
	 * @param req
	 * @param timeout
	 * @return
	 */
	public RequestResult awaitForMSTransmission(RequestObject req, long timeout) {
		Thread thread = Thread.currentThread();// get caller thread.
		
		//TODO: 
		return null;
	}

}
