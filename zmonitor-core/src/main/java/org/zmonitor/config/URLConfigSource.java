/**
 * 
 */
package org.zmonitor.config;

import java.io.IOException;
import java.net.URL;

import org.zmonitor.util.Arguments;
import org.zmonitor.util.DOMRetriever;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class URLConfigSource implements ConfigSource {

	private URL url;
	/**
	 * 
	 */
	public URLConfigSource(URL url) {
		Arguments.checkNotNull(url, "url cannot be null!");
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.ConfigSource#getDOMRetriever()
	 */
	public DOMRetriever getDOMRetriever() throws IOException {
		return new InputStreamConfigSource(url.openStream()).getDOMRetriever();
	}

}
