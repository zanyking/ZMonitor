/**
 * 
 */
package org.zmonitor.config;

import javax.servlet.ServletContext;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebConfigSource extends InputStreamConfigSource {

	/**
	 * use default contextResourcePath: {@link ConfigSource#WEB_INF_ZMONITOR_XML}
	 * @param servCtx
	 */
	public WebConfigSource(ServletContext servCtx) {
		this(servCtx, WEB_INF_ZMONITOR_XML);
	}
	/**
	 * 
	 * @param servCtx
	 * @param contextResourcePath
	 */
	public WebConfigSource(ServletContext servCtx, 
			String contextResourcePath) {
		super(servCtx.getResourceAsStream(contextResourcePath));
	}


}
