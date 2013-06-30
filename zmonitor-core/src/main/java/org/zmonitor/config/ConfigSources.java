/**
 * 2013/05/02
 */
package org.zmonitor.config;

import static org.zmonitor.config.ConfigSource.DEFAULT_SYS_PROP_NAME;
import static org.zmonitor.config.ConfigSource.ZMONITOR_XML;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.zmonitor.util.Loader;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfigSources {

	private ConfigSources() {
	}
	
	/**
	 * 	To load the default configuration file from a simple Java program, the loading sequence: <br>
	 * <ul>
	 * 	<li>System Property {@link ConfigSource#DEFAULT_SYS_PROP_NAME}</li>
	 *  <li>classpath: {@link ConfigSource#ZMONITOR_XML}.</li>
	 * </ul>
	 * To load the configuration from Web application by yourself, please use {@link WebConfigSource} directly. 
	 * @return 
	 * @throws IOException
	 */
	public static ConfigSource loadForSimpleJavaProgram() throws IOException{
		ConfigSource confSrc = loadFromDefaultSystemProperty();
		if(confSrc==null){
			confSrc = loadFromDefaultClassPath();	
		}
		return confSrc;
	}

	/**
	 * To load the default configuration file from a JavaEE Web Application, the loading sequence: <br>
	 * <ul>
	 * 	<li> application context resource: /WEB-INF/zmonitor.xml {@link ConfigSource#WEB_INF_ZMONITOR_XML}</li>
	 *  <li>Classpath: {@link ConfigSource#ZMONITOR_XML}.</li>
	 * </ul>
	 * @param servCtx
	 * @return
	 * @throws IOException
	 */
	public static ConfigSource loadForJavaEEWebApp(ServletContext servCtx) throws IOException{
		InputStreamConfigSource confSrc = new WebConfigSource(servCtx);
		if(confSrc.hasConfiguration())
			return confSrc;
		else
			return loadFromDefaultClassPath();
	}
	
	/**
	 * 
	 * @param resClassPath the path will be loaded by {@link Loader#getResourceAsStreamIfAny(String)} 
	 * @return
	 * @throws IOException
	 */
	public static ConfigSource loadFromClassPath(String resClassPath) throws IOException{
		ClassPathConfigSource confSrc = new ClassPathConfigSource(resClassPath);
		if(confSrc.hasConfiguration())return confSrc;
		return null;
	}
	/**
	 * 
	 * @return an instance of {@link ConfigSource}, or null if classpath:zmonitor.xml is referenced to nothing.
	 * @throws IOException
	 */
	public static ConfigSource loadFromDefaultClassPath() throws IOException{
		return loadFromClassPath(ZMONITOR_XML);
	}
	/**
	 * using default propertyName: {@link ConfigSource#DEFAULT_SYS_PROP_NAME} to construct a {@link ConfigSource}.
	 * @return an instance of {@link ConfigSource}, or null if no such system property has been found.
	 * @throws IOException
	 */
	public static ConfigSource loadFromDefaultSystemProperty() throws IOException{
		return loadFromSystemProperty(DEFAULT_SYS_PROP_NAME);
	}
	/**
	 * using given property name to construct a {@link ConfigSource}.
	 * @param propertyName the name used to retrieve the URL from system properties. 
	 * @return an instance of {@link ConfigSource}, or null if no such system property has been found.
	 * @throws IOException
	 */
	public static ConfigSource loadFromSystemProperty(String propertyName) throws IOException{
		String urlStr = System.getProperty(propertyName);
		if(urlStr==null || urlStr.length()==0){
			return null;
		}
		return new SysPropConfigSource(propertyName);
	}

}
