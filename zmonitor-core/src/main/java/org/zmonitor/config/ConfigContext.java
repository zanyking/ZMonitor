/**
 * 
 */
package org.zmonitor.config;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.util.PropertySetter;



/**
 * A context object designed for ZMonitor's Configuration phase.<br> 
 * 
 * 
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
	 * @param xPath
	 * @return a new ConfigContext object which currentNode is the first entry of xpath query result, NULL if xpth query returns nothing 
	 */
	public ConfigContext toNode(String xPath);
	
	/**
	 * 
	 * @return the current selected DOM node of configuration(an XML file), 
	 * method such as {@link #toNode(String)} and {@link #forEach(String, Visitor)} 
	 * will based on this node to invoke xpath query. <br>
	 * Bean operations such as {@link #newBean(Class, boolean)} 
	 * will also based on current node selection to perform the action.
	 */
	public Node getNode();
	
	/**
	 * 
	 * @param xPath the xquery result should be a nodeList.
	 * @param visitor a visitor that will visits every entry of xquery result.
	 */
	public void forEach(String xPath, Visitor visitor);
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 * @param <T>
	 */
	interface Visitor{
		/**
		 * 
		 * @param idx
		 * @param node
		 * @param self
		 * @return true if you want to continue, false break the iteration.
		 */
		public boolean visit(int idx, Node node);
	}
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






