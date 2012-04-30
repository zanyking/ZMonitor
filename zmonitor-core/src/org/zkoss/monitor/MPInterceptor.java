/**MPInterceptor.java
 * 2011/4/5
 * 
 */
package org.zkoss.monitor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MPInterceptor {
	
	/**
	 * 
	 * @param mpCtx
	 */
	public void doBeforeCompose(MPContext mpCtx);
	

}
