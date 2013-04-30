/**
 * 
 */
package org.zmonitor.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zmonitor.IgnitionFailureException;
import org.zmonitor.spi.Configurator;
/**
 * to make ZMonitor be adaptable to various container & logger, modulization is required.<br> 
 * this class is a helper class to collect {@link Configurator} implementation from META-INF/zmonitor/configurator  
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfiguratorRepository {
	private static final String CONFIG_PROPS = "META-INF/services/config.properties";
	
	
	/**
	 * 1. Scan all META-INF/zmonitor/config.properties
	 * 2. Instantiate Configurator instance
	 * 3. 
	 */
	public void scan(){
		ClassLoader loader = getClass().getClassLoader();
		Enumeration<URL> configs;
		try {
			configs = loader.getResources(CONFIG_PROPS);
		} catch (IOException e) {
			ZMLog.warn(e, "error happened while loading resources from: ", CONFIG_PROPS);
			throw new IgnitionFailureException(e);
		}
		
		while(configs.hasMoreElements()){
			// each zmonitor-xxx.jar might has one config.props
			URL url = configs.nextElement();
			ConfiguratorContext cCtxt = new ConfiguratorContext();
			load(cCtxt, url);
		}
	}
	
	
	private void load(ConfiguratorContext cCtxt, URL url){
		Def def =  new Def();
		initDef(def, url);
		
		//TODO: according to def, instantiate ConfigContext & Configurators
		
		
	}
	
	private static void initDef(Def def, URL url){
		InputStream in = null;
		BufferedReader r = null;
		try {
		    in = url.openStream();
		    r = new BufferedReader(new InputStreamReader(in, "utf-8"));
		    int lc = 1;
		    while ((lc = parseLine(def, url, r, lc)) >= 0);
		    
		} catch (IOException e) {
			ZMLog.warn(e, "error happened while reading resource: ", url);
		}finally{
			try {
				if (r != null) r.close();
				if (in != null) in.close();
			}catch(IOException y){
				ZMLog.warn(y, "error happened while reading resource: ",  url);
			}
		}
	}
	/**
	 * # is for comment 
	 * 
	 * @param def
	 * @param url
	 * @param r
	 * @param lc
	 * @return
	 * @throws IOException
	 */
	private static int parseLine(Def def, URL url,
			BufferedReader r, int lc) throws IOException {
		// TODO Auto-generated method stub
		String ln = r.readLine();
		if (ln == null) return -1;
		int ci = ln.indexOf('#');// any char after # will be ignored, it's comment.
		if (ci >= 0) ln = ln.substring(0, ci);
		ln = ln.trim();
		int n = ln.length();
		//[key]=[value]
		if (n > 3) {
			int eqPos = ln.indexOf('=');
			if(eqPos<0)
				throw new Error("Illegal config syntax, missing '=' at line:"+lc);
			
			String key =ln.substring(0, eqPos); 
			if ((key.indexOf(' ') >= 0) || (key.indexOf('\t') >= 0))
				throw new Error("Illegal config syntax, no empty or tab allowed in key");
			
			String value = ln.substring(eqPos+1, ln.length());
			def.set(key, value);
		}
		return lc+1;
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	private class Def{
		Map<String, String> attrs = new LinkedHashMap<String, String>();

		public void set(String key, String value){
			attrs.put(key, value);
		}
		
	}//end of class...
}
