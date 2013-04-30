package org.zmonitor.impl.config;

import java.io.IOException;
import java.io.InputStream;

import org.zmonitor.ConfigSource;
import org.zmonitor.util.DOMRetriever;
/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class InputStreamConfigSource implements ConfigSource {

	private InputStream in;
	public InputStreamConfigSource(InputStream in) {
		this.in = in;
	}

	public DOMRetriever getDOMRetriever() throws IOException {
		DOMRetriever xmlDoc = null;
		if(in !=null){
			try {
				xmlDoc = new DOMRetriever(in);
			}finally{
				in.close();
			}	
		}
		return xmlDoc;
	}

}
