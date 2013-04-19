/**
 * 
 */
package org.zmonitor.spi;





/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public interface MonitorPointInfoFactory {
	
	/**
	 * 
	 * @param sElemt could be null, if user do not want this.
	 * @param mpType
	 * @return
	 */
	public Name getName(StackTraceElement sElemt, String mpType);
	/**
	 * 
	 * @param sElemt could be null, if user do not want this.
	 * @param mpType
	 * @return
	 */
	public String getMessage(StackTraceElement sElemt, String mpType);
}
