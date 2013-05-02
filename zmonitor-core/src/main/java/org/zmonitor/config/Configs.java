/**
 * 
 */
package org.zmonitor.config;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Configs {
	private Configs(){}
	
	
	/**
	 * 
	 * @param configCtx
	 * @param customConf
	 * @param shouldApplyProperties
	 */
	public static void initCustomConfigurable(ConfigContext configCtx, 
			CustomConfigurable customConf, 
			boolean shouldApplyProperties){
		if(shouldApplyProperties){
			configCtx.applyPropertyTags(new PropertySetter(customConf));
		}
		customConf.configure(configCtx);
		configCtx.getManager().register(customConf);
	}
}
