/**
 * 
 */
package org.zmonitor;

import java.io.InputStream;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ResourceProvider {

	/**
	 * retrieve resource as an input stream according to the given path.  
	 * @param path the path to retrieve an input stream, the path might be an URL, a WebContext path or etc...
	 * @return an inputstream or null if there's no mapping resource to the given path.
	 */
	InputStream getResourceAsInputStream(String path);
	
}
