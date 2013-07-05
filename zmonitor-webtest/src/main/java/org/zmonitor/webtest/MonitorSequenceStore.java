/**
 * 
 */
package org.zmonitor.webtest;

import java.util.Iterator;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorSequenceStore {
	/**
	 * 
	 * @param key
	 * @return
	 */
	public MonitorSequence find(String key);
	/**
	 * 
	 * @return
	 */
	public Iterator<MonitorSequence> iterator();
}
