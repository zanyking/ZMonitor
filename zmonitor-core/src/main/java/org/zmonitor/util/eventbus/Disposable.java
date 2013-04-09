/**AutoDisposable.java
 * 2011/7/19
 * 
 */
package org.zmonitor.util.eventbus;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 *
 *	A event listener which implement this interface will be disposed while shouldDispose() return true,
 *  A disposed EventListener will be unlisten and never been triggered by event bus    
 */
public interface Disposable {

	/**
	 * to tell the Event bus whether this Event Listener instance should being kept or dispose.
	 * @return true, if this eventListener instance should be disposed, false otherwise.
	 */
	public boolean isDisposable();
}
