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
import java.util.List;

import org.zmonitor.IgnitionFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.ConfigurationError;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.XMLConfiguration;
/**
 * to make ZMonitor be adaptable to various container & logger, modulization is required.<br> 
 * this class is a helper class to collect {@link Configurator} implementation from META-INF/zmonitor/configurator  
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfiguratorRepository {
	private static final String CONFIG_PROPS = "META-INF/services/config.properties";
	private static final String CONFIGURATORS = "configurators";
	private static final String KEY_CONFIG_URL = "__configURL";
	
	private List<ConfiguratorContextImpl> confCtxs = 
			new ArrayList<ConfiguratorContextImpl>(10);
	
	/**
	 * 1. Scan all META-INF/zmonitor/config.properties
	 * 2. Instantiate ConfigContext instances
	 * 3. 
	 */
	public void scan(){
		ClassLoader loader = getClass().getClassLoader();
		Enumeration<URL> confUrls;
		try {
			confUrls = loader.getResources(CONFIG_PROPS);
		} catch (IOException e) {
			ZMLog.warn(e, 
				"error happened while loading resources from: ", CONFIG_PROPS);
			
			throw new IgnitionFailureException(e);
		}
		
		while(confUrls.hasMoreElements()){
			// each zmonitor-xxx.jar might has one config.props
			URL url = confUrls.nextElement();
			ConfiguratorContextImpl cCtxt = new ConfiguratorContextImpl();
			cCtxt.set(KEY_CONFIG_URL, url.toString());
			initConfigCtxt(cCtxt, url);
			confCtxs.add(cCtxt);
		}
	}
	
	/**
	 * 
	 * @param manager
	 * @param configuration
	 */
	public void performConfiguration(ZMonitorManager manager, XMLConfiguration configuration){
		for (ConfiguratorContextImpl confCtx : confCtxs) {
			confCtx.setConfiguration(configuration);
			confCtx.setManager(manager);
			try {
				String val = confCtx.get(CONFIGURATORS);
				if(val==null){
					throw new ConfigurationError(
							"must declare ["+CONFIGURATORS+ "] in: "+confCtx.get(KEY_CONFIG_URL));
				}
				
				String[] clzArr = val.split("[,]");
				for(String clzStr : clzArr){
					Configurator configurator = 
						(Configurator) Class.forName(clzStr).newInstance();
					configurator.configure(confCtx);
				}
			} catch (Exception e) {
				throw new ConfigurationError(e);
			}
		}
	}
	
	
	/**
	 * the format of config.properties
	 * <ul>
	 * 	<li> utf8 encoded
	 * 	<li> [key]=[value]
	 * 	<li> the first equal sign( = ) will be treated as the separator.
	 * 	<li>key should not contain any spaces or tabs.
	 * </ul>
	 * @param def
	 * @param url
	 */
	private static void initConfigCtxt(ConfiguratorContextImpl def, URL url){
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
	 * UTF-8 encoded
	 * 
	 * @param def
	 * @param url
	 * @param r
	 * @param lc
	 * @return
	 * @throws IOException
	 */
	private static int parseLine(ConfiguratorContextImpl def, URL url,
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
	
}

