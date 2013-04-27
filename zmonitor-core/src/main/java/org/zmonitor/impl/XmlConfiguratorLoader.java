/**XmlConfiguratorLoader.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.Loader;


/**
 * TODO must be teared down
 * The loader class for {@link CoreConfigurator}
 * @see org.zmonitor.impl.CoreConfigurator 
 * @author Ian YT Tsai(Zanyking)
 */
public class XmlConfiguratorLoader {
	public static final String ZMONITOR_XML = "zmonitor.xml";
	public static final String WEB_INF_ZMONITOR_XML = "/WEB-INF/"+ZMONITOR_XML;
	public static final String DEFAULT_SYS_PROP_NAME = "zmonitor.config.url";
	/**
	 *  The Sequence of zmonitor.xml loading if ZMonitor is used in a Pure Java Program:
	 * <ul>
	 * 	<li>Return {@link #loadFromDefaultSystemProperty()} if it has value.</li>
	 *  <li>get configuration from java class path, could be null.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws IOException
	 */
	public static CoreConfigurator loadForPureJavaProgram() throws IOException{
		CoreConfigurator xConf = loadFromDefaultSystemProperty(); 
		if(xConf!=null)return xConf;
		return loadFromClassPath();
	}
	/**
	 * The Sequence of zmonitor.xml loading if ZMonitor is used in a Java EE Web Application:
	 * 
	 * <ul>
	 * 	<li>Return {@link #loadFromDefaultSystemProperty()} if it has value.</li>
	 * 	<li>If "/WEB-INF/zmonitor.xml" exist in this application, use it.</li>
	 *  <li>get configuration from java class path, could be null.</li>
	 * </ul>
	 * @param servCtx use this to get resource from "/WEB-INF/zmonitor.xml" by calling: 
	 * {@link ServletContext#getResourceAsStream(String)}.
	 * @return an XmlConfigurator comes from a Java EE Web Application.
	 * @throws IOException 
	 */
	public static CoreConfigurator loadForJavaEEWebApp(ServletContext servCtx) throws IOException{
		
		CoreConfigurator xConf = loadFromDefaultSystemProperty(); 
		if(xConf!=null)return xConf;
		
		DOMRetriever xmlDoc = newInstance(servCtx.getResourceAsStream(WEB_INF_ZMONITOR_XML));
		xConf = newInstance(xmlDoc);
		if(xConf!=null)return xConf;
		
		return loadFromClassPath();
	}
	
	/**
	 * Construct configuration instance: {@link XmlConfigurator) using default propertyName: "zmonitor.config.url".
	 * @return an instance of {@link CoreConfigurator}, null if no such system property found.
	 * @throws IOException
	 */
	public static CoreConfigurator loadFromDefaultSystemProperty() throws IOException{
		String urlStr = System.getProperty(DEFAULT_SYS_PROP_NAME);
		if(urlStr==null || urlStr.length()==0){
			return null;
		}
		InputStream in = null;
		DOMRetriever xmlDoc = null;
		try {
			URL url = new URL(urlStr);
			xmlDoc = new DOMRetriever(in = url.openStream());
			
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} finally{
			if(in!=null)in.close();	
		}
		return newInstance(xmlDoc);
	}
	/**
	 * Construct configuration instance: {@link XmlConfigurator) using given propertyName.
	 * @param propertyName the name used to retrieve the URL String value from system properties. 
	 * @return a {@link CoreConfigurator} instance.
	 * @throws IOException
	 */
	public static CoreConfigurator loadFromSystemProperty(String propertyName) throws IOException{
		String urlStr = System.getProperty(propertyName);
		if(urlStr==null || urlStr.length()==0){
			return null;
		}
		InputStream in = null;
		DOMRetriever xmlDoc = null;
		try {
			URL url = new URL(urlStr);
			xmlDoc = new DOMRetriever(in = url.openStream());
			
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} finally{
			if(in!=null)in.close();	
		}
		return newInstance(xmlDoc);
	}
	/**
	 * using default Java Class Path "/zmonitor.xml" as the configuration location.
	 * @return a {@link CoreConfigurator} instance.
	 * @throws IOException
	 */
	public static CoreConfigurator loadFromClassPath() throws IOException{
		return loadFromClassPath(ZMONITOR_XML);
	}
	/**
	 * 
	 * @param classPath the given resource path using current thread's ClassLoder or 
	 * ZMonitor's ClassLoader as context.
	 *  
	 * @return
	 * @throws IOException
	 */
	public static CoreConfigurator loadFromClassPath(String classPath) throws IOException{
		
		DOMRetriever xmlDoc = newInstance(Loader.getResourceAsStreamIfAny(classPath));
		return newInstance(xmlDoc);
	}
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static CoreConfigurator loadFromClassPath(URL url) throws IOException{
		DOMRetriever xmlDoc = newInstance(url.openStream());
		return newInstance(xmlDoc);
	}
	// 
	private static CoreConfigurator newInstance(DOMRetriever xmlDoc){
		return new CoreConfigurator();
	}
	// 
	private static DOMRetriever newInstance(InputStream in) throws IOException{
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
