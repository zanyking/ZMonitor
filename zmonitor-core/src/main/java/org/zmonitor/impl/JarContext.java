/**
 * 
 */
package org.zmonitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zmonitor.spi.LookUpError;
import org.zmonitor.spi.Configurator;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JarContext {
	private Map<String, String> attrs = new HashMap<String, String>();
	private List<Configurator> configurators;
	private static final String CONFIGURATORS = "configurators";
	/**
	 * 
	 */
	public JarContext() {}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value){
		attrs.put(key, value);
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> getAttrs(){
		return attrs;
	}
	/**
	 * 
	 */
	public void init(){
		String val = attrs.get(CONFIGURATORS);
		if(val==null){
			throw new LookUpError(
					"must declare ["+CONFIGURATORS+ "] in: "+
					attrs.get(ConfiguratorRepository.KEY_CONFIG_URL));
		}
		String[] clzArr = val.split("[,]");
		configurators = new ArrayList<Configurator>(clzArr.length);
		for(String clzStr : clzArr){
			try {
				configurators.add(
					(Configurator)Class.forName(clzStr).newInstance());
			} catch (Exception e) {
				throw new LookUpError(e);
			}
		}
	}
	/**
	 * 
	 * @return
	 */
	public List<Configurator> getConfigurators(){
		return configurators;
	}
}
