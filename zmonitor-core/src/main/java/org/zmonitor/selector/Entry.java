/**
 * 
 */
package org.zmonitor.selector;

import java.util.Set;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Entry {
	/**
	 * 
	 * @return
	 */
	EntryContainer getEntryContainer();
	/**
	 * 
	 * @return
	 */
	Entry getParent();
	/**
	 * 
	 * @return
	 */
	int getIndex();
	/**
	 * 
	 * @return
	 */
	int size();
	/**
	 * 
	 * @return
	 */
	boolean isEmpty();
	/**
	 * 
	 * @return
	 */
	Entry getNextSibling();
	/**
	 * 
	 * @return
	 */
	Entry getPreviousSibling();
	/**
	 * 
	 * @return
	 */
	Entry getFirstChild();
	/**
	 * 
	 * @return
	 */
	Object getObject();
	/**
	 * 
	 * @return
	 */
	String getType();
	/**
	 * 
	 * @return
	 */
	String getId();
	/**
	 * 
	 * @return
	 */
	Set<String> getConceptualCssClasses();
	
}
