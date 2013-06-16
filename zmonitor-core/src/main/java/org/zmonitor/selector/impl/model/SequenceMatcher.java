/**
 * 
 */
package org.zmonitor.selector.impl.model;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface SequenceMatcher {
	/**
	 * 
	 * @param sequence
	 * @param target
	 * @return
	 */
	boolean matches(SimpleSelectorSequence sequence);
}
