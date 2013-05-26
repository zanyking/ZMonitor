/**
 * 
 */
package org.zmonitor.selector.impl;


/**
 * The model of pseudo class definition
 * @author simonpai
 */
public interface PseudoClassDef {
	
	/**
	 * Return true if the component qualifies this pseudo class.
	 * @param ctx 
	 * @param parameters
	 * @return
	 */
	public boolean accept(MatchCtx ctx, String ... parameters);
	
}
