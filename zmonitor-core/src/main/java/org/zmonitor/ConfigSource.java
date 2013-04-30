/**
 * 
 */
package org.zmonitor;

import java.io.IOException;

import org.zmonitor.util.DOMRetriever;

/**
 * Configuration might come from different place( file, DB, http, )
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ConfigSource {
	public static final String DEFAULT_SYS_PROP_NAME = "zmonitor.config.url";
	public static final String ZMONITOR_XML = "zmonitor.xml";
	public static final String WEB_INF_ZMONITOR_XML = "/WEB-INF/"+ZMONITOR_XML;
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public DOMRetriever getDOMRetriever()throws IOException;
}
