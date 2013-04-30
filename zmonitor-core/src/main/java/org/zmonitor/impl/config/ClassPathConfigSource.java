/**
 * 
 */
package org.zmonitor.impl.config;

import org.zmonitor.util.Loader;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ClassPathConfigSource extends InputStreamConfigSource{
	
	/**
	 * 
	 */
	public ClassPathConfigSource(String classPathSrc) {
		super(Loader.getResourceAsStreamIfAny(classPathSrc));
	}

	public ClassPathConfigSource(){
		this(ZMONITOR_XML);
	}

}
