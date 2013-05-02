/**
 * 
 */
package org.zmonitor.spi;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.XMLConfiguration.Visitor;
import org.zmonitor.util.PropertySetter;



/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface ConfigContext {
	/**
	 * attributes the config.properties provided.
	 * @param key the key to access property
	 * @return value 
	 */
	public String get(String key);
	/**
	 * 
	 * @return the manager that Configurator should manipulated.
	 */
	public ZMonitorManager getManager();
	/**
	 * 
	 * @return
	 */
	public XMLConfiguration getConfiguration();
	/**
	 * 
	 * @param xPath
	 * @return
	 */
	public ConfigContext query(String xPath);
	
	/**
	 * 
	 * @return
	 */
	public Node getNode();
	
	/**
	 * @param xPath
	 * @param visitor
	 */
	public void forEach(String xPath, Visitor visitor);
	/**
	 * 
	 * @param setter
	 */
	public void applyPropertyTags(PropertySetter setter);
	/**
	 * 
	 * @param setter
	 * @param ignores
	 */
	public void applyAttributes( PropertySetter setter, String... ignores);
	
	/**
	 * 
	 * @param defaultClass
	 * @param mustHave
	 * @return
	 */
	public <T> T newBean(Class<T> defaultClass, boolean mustHave);
}






