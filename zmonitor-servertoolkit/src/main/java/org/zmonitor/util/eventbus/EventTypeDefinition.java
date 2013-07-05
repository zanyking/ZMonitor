/**
 * 
 */
package org.zmonitor.util.eventbus;

import java.lang.reflect.Method;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public interface EventTypeDefinition<K>{
	/**
	 * the base event type that any future event need to based on. 
	 * @return
	 */
	public Class<K> getBaseEventType();
	/**
	 * 
	 * @param event the event Object that will used as parameter while listening methods invocation.
	 * @return the Actual Type that the given event maps to.
	 */
	public Class<? extends K> resolveEventClass(Object event); 
	/**
	 * Verifying the input method is an event listening method or not<br> 
	 * if this is an event listening method, then a proper event type should returned.
	 *  
	 * @param method the method which need to be verified.
	 * @return null if the type is not match Event type, instance if there's a match.
	 */
	public Class<? extends K> retrieveListeningEventType(Method method);
	
	
	
}
