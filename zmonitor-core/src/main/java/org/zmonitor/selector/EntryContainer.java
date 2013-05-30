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
public interface EntryContainer {

	/**
	 * 
	 * @return
	 */
	Entry getFirstRoot();
	
	/**
	 * 
	 * @return
	 */
	Object getObject();
	/**
	 * 
	 * @return
	 */
	int size();

}
