/**
 * 
 */
package org.zmonitor.web;

import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;
import static org.zmonitor.impl.XMLConfigs.getTextFromAttrOrContent;

import org.w3c.dom.Node;
import org.zmonitor.CustomConfigurable;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.Configs;
import org.zmonitor.config.ConfigContext.Visitor;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.web.filter.Condition;
import org.zmonitor.web.filter.DefaultUrlFilter;
import org.zmonitor.web.filter.UrlFilter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebConfigurator extends ZMBeanBase implements Configurator {
	
	public static final String REL_WEB_CONF = "web-conf";
	private static final String REL_URL_FILTER = "url-filer";
	private static final String REL_CONDITION = "condition";

	private UrlFilter filter;
	
	/**
	 * 
	 * @param urlStr
	 * @return
	 */
	public boolean shouldAccept(String urlStr){
		if(filter==null)return true;
		return filter.shouldAccept(urlStr);
	}
	
	public void configure(ConfigContext monitorMgmt) {
		ConfigContext webConf = monitorMgmt.toNode(REL_WEB_CONF);
		
		if(webConf.getNode()==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initUrlFilter(webConf);
		
	}
	
	
	private void initUrlFilter(ConfigContext webConf){
		ConfigContext urlFilterCtx = webConf.toNode(REL_URL_FILTER);
		if(urlFilterCtx.getNode()==null)return;
		
		filter = urlFilterCtx.newBean(DefaultUrlFilter.class, false);
		
		urlFilterCtx.applyAttributes(new PropertySetter(filter), "class");
		
		if(filter instanceof DefaultUrlFilter){
			initUrlFilterByDefault(urlFilterCtx, (DefaultUrlFilter)filter);
			
		} else if(filter instanceof CustomConfigurable){
			Configs.initCustomConfigurable(urlFilterCtx, 
					(CustomConfigurable) filter, 
					true);
		}
	}
	
	private static UrlFilter initUrlFilterByDefault(
			final ConfigContext urlFilterCtx, 
			final DefaultUrlFilter filter){

		urlFilterCtx.forEach( REL_CONDITION, new Visitor(){
			public boolean visit(int idx, ConfigContext nodeCtx) {
				
				Condition cond = new Condition();
				PropertySetter setter = new PropertySetter(cond);
				
				urlFilterCtx.applyPropertyTags(setter);
				nodeCtx.applyAttributes(setter);
				
				if(cond.getPattern()==null){
					cond.setPattern(nodeCtx.getAttribute("pattern"));
				}
				filter.add(cond);
				return true;
			}});
		
		return filter;
	}
	
		
}
