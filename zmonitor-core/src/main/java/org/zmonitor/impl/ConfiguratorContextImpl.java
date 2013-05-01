/**
 * 
 */
package org.zmonitor.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.XMLConfiguration;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConfiguratorContextImpl implements ConfigContext{

	
	private Map<String, String> attrs = new HashMap<String, String>();
	
	private ZMonitorManager zMonitorManager;
	private XMLConfiguration configuration;
	
	
	
	
	public void set(String key, String value){
		attrs.put(key, value);
	}
	public String get(String key){
		return attrs.get(key);
	}
	
	public void setManager(ZMonitorManager zMonitorManager) {
		this.zMonitorManager = zMonitorManager;
	}
	
	public ZMonitorManager getManager() {
		return zMonitorManager;
	}
	
	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public XMLConfiguration getConfiguration() {
		return configuration;
	}


	private List<Configurator> configurators = new ArrayList<Configurator>();
	/**
	 * 
	 * @param conf
	 */
	public void addConfigurator(Configurator conf){
		configurators.add(conf);
	}
	/**
	 * 
	 * @param manager
	 * @param configuration
	 */
	public void doConfigure(ZMonitorManager manager, 
			XMLConfiguration configuration){
		
		for(Configurator conf : configurators){
			conf.configure(this);
		}
	}

}
