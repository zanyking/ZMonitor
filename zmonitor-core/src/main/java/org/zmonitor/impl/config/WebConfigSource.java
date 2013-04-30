/**
 * 
 */
package org.zmonitor.impl.config;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.zmonitor.ConfigSource;
import org.zmonitor.util.DOMRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebConfigSource implements ConfigSource {

	private ServletContext servCtx;
	private String contextResourcePath = WEB_INF_ZMONITOR_XML;
	/**
	 * 
	 */
	public WebConfigSource(ServletContext servCtx) {
		this.servCtx = servCtx;
	}
	/**
	 * 
	 */
	public WebConfigSource(String contextResourcePath, ServletContext servCtx) {
		this.servCtx = servCtx;
		this.contextResourcePath = contextResourcePath;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.ConfigSource#getDOMRetriever()
	 */
	public DOMRetriever getDOMRetriever() throws IOException {
		DOMRetriever xmlDoc = new InputStreamConfigSource(
			servCtx.getResourceAsStream(contextResourcePath)).getDOMRetriever();
		return xmlDoc;
	}

}
