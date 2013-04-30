/**JavaWebConfiguration.java
 * 2011/4/6
 * 
 */
package org.zmonitor.web;


import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;
import static org.zmonitor.impl.XMLConfigs.getTextFromAttrOrContent;
import static org.zmonitor.impl.XMLConfigs.ignores;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.CoreConfigurator;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.spi.XMLConfiguration.Visitor;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.web.filter.Condition;
import org.zmonitor.web.filter.DefaultUrlFilter;
import org.zmonitor.web.filter.UrlFilter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JavaWebConfiguration implements CustomConfiguration {

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
	/*
	 * (non-Javadoc)
	 * @see org.zmonitor.CustomConfiguration#apply(org.zmonitor.ZMonitorManager, org.zmonitor.util.DOMRetriever, org.w3c.dom.Node)
	 */
	public void apply(ZMonitorManager manager, 
			final XMLConfiguration config, 
			Node configNode) {
		Node urlFilterNode = config.getNode(REL_URL_FILTER, configNode);
		if(urlFilterNode==null)return;
		
		filter = newInstanceByClassAttr(urlFilterNode, DefaultUrlFilter.class, false);
		
		applyAttributesToBean( 
				urlFilterNode, 
				new PropertySetter(filter), 
				ignores("class"));
		
		if(filter instanceof DefaultUrlFilter){
			initUrlFilterByDefault(config, urlFilterNode, (DefaultUrlFilter)filter);
			
		} else if(filter instanceof  CustomConfiguration){
			CoreConfigurator.initCustomConfiguration(manager, 
					config, 
					urlFilterNode, 
					(CustomConfiguration) filter, 
					true);
		}
		
	}
	
	private static UrlFilter initUrlFilterByDefault(
			final XMLConfiguration config, 
			Node urlFilterNode, 
			final DefaultUrlFilter filter){
		applyAttributesToBean( urlFilterNode, new PropertySetter(filter), null);

		config.forEach(urlFilterNode, REL_CONDITION, new Visitor(){
			public boolean visit(int idx, Node node) {
				Condition cond = new Condition();
				config.applyPropertyTagsToBean( node, new PropertySetter(cond));
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
