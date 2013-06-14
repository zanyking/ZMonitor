/**
 * 
 */
package org.zmonitor.selector;

import java.util.Set;

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
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public String retrieveId(E obj);
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public String retrieveType(E obj);
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public  Set<String> retrieveConceptualCssClasses(E obj);
	/**
	 * 
	 * @param varName
	 * @param e
	 * @return
	 */
	public Object resolveAttribute( String varName, Entry<E> e);
	
}
