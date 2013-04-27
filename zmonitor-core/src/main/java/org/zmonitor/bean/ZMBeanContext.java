/**
 * 
 */
package org.zmonitor.bean;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZMBeanContext {
	private ZMBeanRepository zMBeanRepository;

	
	/**
	 * 
	 * @param zMBeanRepository
	 */
	public ZMBeanContext(ZMBeanRepository zMBeanRepository) {
		this.zMBeanRepository = zMBeanRepository;
	}

	/**
	 * 
	 * @return
	 */
	public ZMBeanRepository getZMBeanRepository() {
		return zMBeanRepository;
	}
	
	
	
}
