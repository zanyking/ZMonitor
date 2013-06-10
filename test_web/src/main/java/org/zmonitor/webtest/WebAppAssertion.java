/**
 * 
 */
package org.zmonitor.webtest;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface WebAppAssertion {

	/**
	 * See if this assertion is suitable to validate a request's monitor sequence.    
	 * @param url
	 * @return
	 */
	boolean matches(String url);
	
	/**
	 * 
	 * @param asrtCtx
	 * 				for implementation to interact with monitor sequence and
	 * 				see if 
	 * 	  
	 * @throws Exception
	 *             any error (include AssertionError) will be handled and
	 *             forwarded to an identical system that has the ability 
	 *             to notify user. 
	 */
	void doAssert(AssertContext asrtCtx)throws Exception;
	
}
