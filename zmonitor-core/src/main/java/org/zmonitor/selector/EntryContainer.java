/**
 * 
 */
package org.zmonitor.selector;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 * @param <E> Type of Entry's object
 * @param <C> Type of EntryContainer's object
 */
public interface EntryContainer<E> {

	/**
	 * 
	 * @return
	 */
	Entry<E> getFirstRoot();
	
	/**
	 * 
	 * @return
	 */
	int size();

}
