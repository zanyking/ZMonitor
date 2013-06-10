/**
 * 
 */
package org.zmonitor.webtest;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class WebAppAssertionBase implements WebAppAssertion {

	public boolean matches(String url) {
		
		return false;
	}

	public void doAssert(AssertContext asrtCtx) throws Exception {

	}

}
