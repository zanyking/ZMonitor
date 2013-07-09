/**
 * 
 */
package org.zmonitor.webtest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorSequenceStore{
	/**
	 * 
	 * @return
	 */
	List<MonitorSequence> drainAll() ;
	/**
	 * 
	 * @param key
	 * @param ms
	 */
	void add( MonitorSequence ms);
	/**
	 * 
	 * @param mss
	 */
	void addAll( Collection<MonitorSequence> mss);
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public boolean contains(String uuid);
}
