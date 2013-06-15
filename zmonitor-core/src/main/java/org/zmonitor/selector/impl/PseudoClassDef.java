/**
 * 
 */
package org.zmonitor.selector.impl;

import org.zmonitor.selector.Entry;


/**
 * The model of pseudo class definition
 * @author simonpai
 */
public interface PseudoClassDef<E> {
	
	/**
	 * Return true if the component qualifies this pseudo class.
	 * @param ctx 
	 * @param parameters
	 * @return
	 */
	public boolean accept(Entry<E> ctx, String ... parameters);
	
}
