/**
 * 
 */
package org.zmonitor.selector;

import java.util.Set;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Entry<E> {
	/**
	 * 
	 * @return
	 */
	EntryContainer<E> getEntryContainer();
	/**
	 * 
	 * @return
	 */
	Entry<E> getParent();
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
	Entry<E> getNextSibling();
	/**
	 * 
	 * @return
	 */
	Entry<E> getPreviousSibling();
	/**
	 * 
	 * @return
	 */
	Entry<E> getFirstChild();
	/**
	 * 
	 * @return
	 */
	E getValue();
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
