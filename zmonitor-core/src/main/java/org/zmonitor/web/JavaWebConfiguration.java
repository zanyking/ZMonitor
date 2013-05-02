/**JavaWebConfiguration.java
 * 2011/4/6
 * 
 */
package org.zmonitor.web;


import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;
import static org.zmonitor.impl.XMLConfigs.getTextFromAttrOrContent;

import org.w3c.dom.Node;
import org.zmonitor.CustomConfigurable;
import org.zmonitor.impl.CoreConfigurator;
import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.XMLConfiguration.Visitor;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.web.filter.Condition;
import org.zmonitor.web.filter.DefaultUrlFilter;
import org.zmonitor.web.filter.UrlFilter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JavaWebConfiguration implements CustomConfigurable {

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
	
	
	public void configure(ConfigContext webConf) {
	
		ConfigContext urlFilterCtx = webConf.query(REL_URL_FILTER);
		if(urlFilterCtx.getNode()==null)return;
		
		filter = urlFilterCtx.newBean(DefaultUrlFilter.class, false);
		
		urlFilterCtx.applyAttributes(new PropertySetter(filter), "class");
		
		if(filter instanceof DefaultUrlFilter){
			initUrlFilterByDefault(urlFilterCtx, (DefaultUrlFilter)filter);
			
		} else if(filter instanceof CustomConfigurable){
			CoreConfigurator.initCustomConfiguration(urlFilterCtx, 
					(CustomConfigurable) filter, 
					true);
		}
		
	}
	
	private static UrlFilter initUrlFilterByDefault(
			final ConfigContext urlFilterCtx, 
			final DefaultUrlFilter filter){

		urlFilterCtx.forEach( REL_CONDITION, new Visitor(){
			public boolean visit(int idx, Node node) {
				Condition cond = new Condition();
				urlFilterCtx.applyPropertyTags(new PropertySetter(cond));
				applyAttributesToBean( node, new PropertySetter(cond), null);
				if(cond.getPattern()==null){
					cond.setPattern(getTextFromAttrOrContent(node, "pattern"));
				}
				filter.add(cond);
				return true;
			}});
		
		return filter;
	}

	
	

}
