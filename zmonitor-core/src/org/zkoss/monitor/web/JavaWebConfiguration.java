/**JavaWebConfiguration.java
 * 2011/4/6
 * 
 */
package org.zkoss.monitor.web;

import static org.zkoss.monitor.impl.XMLConfigs.*;



import org.w3c.dom.Node;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.impl.XMLConfigurator;
import org.zkoss.monitor.spi.CustomConfiguration;
import org.zkoss.monitor.util.NodeIterator;
import org.zkoss.monitor.util.PropertySetter;
import org.zkoss.monitor.util.DOMRetriever;
import org.zkoss.monitor.web.filter.Condition;
import org.zkoss.monitor.web.filter.DefaultUrlFilter;
import org.zkoss.monitor.web.filter.UrlFilter;

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
	 * @see org.zkoss.monitor.CustomConfiguration#apply(org.zkoss.monitor.ZMonitorManager, org.zkoss.monitor.util.DOMRetriever, org.w3c.dom.Node)
	 */
	public void apply(ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node configNode) {
		Node urlFilterNode = xmlDoc.getNodeList(configNode, REL_URL_FILTER).item(0);
		if(urlFilterNode==null)return;
		
		filter = newInstanceByClassAttr(urlFilterNode, DefaultUrlFilter.class, false);
		if(filter!=null){
			applyAttributesToBean(xmlDoc, 
					urlFilterNode, 
					new PropertySetter(filter), 
					getIgnores("class"));
			if(filter instanceof DefaultUrlFilter){
				initUrlFilterByDefault(xmlDoc, urlFilterNode, (DefaultUrlFilter)filter);
				
			} else if(filter instanceof  CustomConfiguration){
				XMLConfigurator.initCustomConfiguration(manager, 
						xmlDoc, 
						urlFilterNode, 
						(CustomConfiguration) filter, 
						true);
			}
		}
		
	}
	
	private static UrlFilter initUrlFilterByDefault(final DOMRetriever xmlDoc, 
			Node urlFilterNode, 
			DefaultUrlFilter filter){
		applyAttributesToBean(xmlDoc, urlFilterNode, new PropertySetter(filter), null);

		new NodeIterator<DefaultUrlFilter>() {// foreach all conditions
			protected void forEach(int index, final Node node, DefaultUrlFilter filter) {
				Condition cond = new Condition();
				applyPropertyTagsToBean(xmlDoc, node, new PropertySetter(cond));
				applyAttributesToBean(xmlDoc, node, new PropertySetter(cond), null);
				if(cond.getPattern()==null){
					cond.setPattern(getTextFromAttrOrContent(node, "pattern"));
				}
				filter.add(cond);
			}
		}.iterate(xmlDoc.getNodeList(urlFilterNode, REL_CONDITION), (DefaultUrlFilter) filter);
		return filter;
	}

}
