/**
 * 
 */
package org.zmonitor.slf4j;

import java.util.Map;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Message {
	/**
	 * 
	 * @return
	 */
	Object[] toArray();
	/**
	 * 
	 * @return
	 */
	Map<String, Object> toMap();

	
}
