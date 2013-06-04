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

import org.zmonitor.InitFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.DOMRetriever;
/**
 * to make ZMonitor be adaptable to various container & logger, modulization is required.<br> 
 * this class is a helper class to collect {@link Configurator} implementation from META-INF/zmonitor/configurator  
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfiguratorRepository {
	static final String CONFIG_PROPS = "META-INF/zmonitor/config.properties";
	static final String KEY_CONFIG_URL = "__configURL";
	private static final String ABS_PROFILING_MANAGER = "/zmonitor";
	private List<JarContext> jarCtxs = 
			new ArrayList<JarContext>(10);
	
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
			
			throw new InitFailureException(e);
		}
		
		while(confUrls.hasMoreElements()){
			// each zmonitor-xxx.jar might has one config.props
			URL url = confUrls.nextElement();
			ZMLog.info("load zmonitor configurators from: ", url);
			JarContext jCtx = new JarContext();
			jCtx.set(KEY_CONFIG_URL, url.toString());
			initJarContext(jCtx, url);
			jarCtxs.add(jCtx);
		}
	}
	
	/**
	 * 
	 * @param manager
	 */
	public void performConfiguration(ZMonitorManager manager){
		DOMRetriever domRetriever;
		try {
			domRetriever = manager.getConfigSource().getDOMRetriever();
		} catch (IOException e) {
			throw new InitFailureException(e);
		}
		ConfigContext confCtx;
		for (JarContext jCtx : jarCtxs) {
			for(Configurator configurator : jCtx.getConfigurators()){
				confCtx = new ConfigContextImpl(jCtx.getAttrs(), manager, domRetriever, null);
				confCtx = confCtx.toNode(ABS_PROFILING_MANAGER);//to <zmonitor> element.
				configurator.configure(confCtx);
				manager.accept(configurator);// test if configurator is a ZMBean.
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
	 * @param jCtx
	 * @param url
	 */
	private static void initJarContext(JarContext jCtx, URL url){
		InputStream in = null;
		BufferedReader r = null;
		try {
		    in = url.openStream();
		    r = new BufferedReader(new InputStreamReader(in, "utf-8"));
		    int lc = 1;
		    while ((lc = parseLine(jCtx, url, r, lc)) >= 0);
		    
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
		jCtx.init();
	}
	/**
	 * # is for comment 
	 * UTF-8 encoded
	 * @param jCtx
	 * @param url
	 * @param r
	 * @param lc
	 * @return
	 * @throws IOException
	 */
	private static int parseLine(JarContext jCtx, URL url,
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
			jCtx.set(key, value);
		}
		return lc+1;
	}
	
}

