/**CoreConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.applyPropertyTagsToBean;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.WrongConfigurationException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.NodeIterator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.Strings;
import org.zmonitor.web.JavaWebConfiguration;

/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class CoreConfigurator  implements Configurator {
	
	private static final String ABS_PROFILING_MANAGER = "/zmonitor";
	
	public static final String REL_MEASURE_POINT_INFO_FACTORY = "mp-info-factory";
	public static final String REL_TIMELINE_HANDLER = "monitor-sequence-handler";
	
	public static final String REL_SERVLET_CONTAINER_CONF = "web-conf";
	

	/**
	 * 
	 */
	public void configure(final ZMonitorManager manager,  ConfigContext ctxt) {
		Node monitorMgmtNode = ctxt.getDomRetriever().getNodeList(ABS_PROFILING_MANAGER).item(0);
		prepareWebConf(manager, ctxt, monitorMgmtNode);
		prepareMPointInfoFacotry(manager, ctxt, monitorMgmtNode);
		prepareMSHandlers(manager, ctxt, monitorMgmtNode);
	}

	private static void prepareWebConf( ZMonitorManager manager, 
			final ConfigContext ctxt, 
			Node monitorMgmtNode){
		Node node = ctxt.getSingleton(REL_SERVLET_CONTAINER_CONF, monitorMgmtNode);
		if(node==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initCustomConfiguration(manager, ctxt, node, new JavaWebConfiguration(), true);
	}
	
	
	
	private static void prepareMPointInfoFacotry(ZMonitorManager manager, 
			ConfigContext ctxt, 
			Node monitorMgmtNode){
		
		Node mpInfoFacNode = ctxt.getSingleton(REL_MEASURE_POINT_INFO_FACTORY, monitorMgmtNode);
		if(mpInfoFacNode==null)return;//use default...
		
		MonitorPointInfoFactory mpInfoFactory = newInstanceByClassAttr(mpInfoFacNode, null, true);
		applyPropertyTagsToBean(ctxt.getDomRetriever(), mpInfoFacNode, new PropertySetter(mpInfoFactory));
		manager.setMonitorPointInfoFactory(mpInfoFactory);
	}
	
	
	private static void prepareMSHandlers(final ZMonitorManager manager, 
			ConfigContext ctxt, 
			Node profilerMgmtNode){
		DOMRetriever xmlDoc = ctxt.getDomRetriever();
		new NodeIterator<DOMRetriever>() {
			protected void forEach(int index, Node node, DOMRetriever xmlDoc) {
				MonitorSequenceHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMRetriever.getAttributeValue(node, NAME);
				applyPropertyTagsToBean(xmlDoc, node, new PropertySetter(handler));
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				manager.addMonitorSequenceHandler(name, handler);
//				if(handler instanceof CustomConfiguration){
//					initCustomConfiguration(manager, xmlDoc, node, (CustomConfiguration) handler, false);
//				}
			}
		}.iterate(xmlDoc.getNodeList(profilerMgmtNode, REL_TIMELINE_HANDLER), xmlDoc);
	}
	/**
	 * 
	 * @param manager
	 * @param xmlDoc
	 * @param configNode
	 * @param customConf
	 * @param shouldApplyProperties
	 */
	public static void initCustomConfiguration(ZMonitorManager manager, 
			final ConfigContext ctxt, 
			Node configNode, 
			CustomConfiguration customConf, 
			boolean shouldApplyProperties){
		DOMRetriever xmlDoc = ctxt.getDomRetriever();
		if(shouldApplyProperties){
			applyPropertyTagsToBean(xmlDoc, configNode, new PropertySetter(customConf));
		}
		customConf.apply(manager, xmlDoc, configNode);
		manager.addCustomConfiguration(customConf);
	}

}
