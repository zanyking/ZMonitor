/*
 * 2013/04/30
 */
package org.zmonitor.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.zmonitor.util.DOMRetriever;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SysPropConfigSource implements ConfigSource{
	
	private String syspropName;

	public SysPropConfigSource(){
		this(DEFAULT_SYS_PROP_NAME);
	}
	public SysPropConfigSource(String syspropName) {
		this.syspropName = syspropName;
	}

	
	public DOMRetriever getDOMRetriever() throws IOException {
		String urlStr = System.getProperty(syspropName);
		if(urlStr==null || urlStr.length()==0){
			return null;
		}
		try {
			URL url = new URL(urlStr);
			return new URLConfigSource(url).getDOMRetriever();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} 
	}

}
