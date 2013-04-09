/**Name.java
 * 2011/3/15
 * 
 */
package org.zmonitor.spi;

import java.io.Serializable;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public interface Name extends Serializable {

	/**
	 * 
	 * @return
	 */
	public String getType();
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj);
	/**
	 * 
	 * @return
	 */
	public int hashCode();
	
	
	public String toShortString();
}
