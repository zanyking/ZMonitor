/**XmlProfilerConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;
import static org.zmonitor.impl.XMLConfigs.applyPropertyTagsToBean;
import static org.zmonitor.impl.XMLConfigs.newInstance;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.WrongConfigurationException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.IdFetcher;
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
public class XMLConfigurator implements Configurator {
	
	private static final String ABS_PROFILING_MANAGER = "/zmonitor";
	
	public static final String REL_MEASURE_POINT_INFO_FACTORY = "mp-info-factory";
	public static final String REL_TIMELINE_HANDLER = "timeline-handler";
	public static final String REL_CUSTOM_CONFIGURATION = "configuration";
	
	public static final String REL_SERVLET_CONTAINER_CONF = "servlet-container-conf";
	public static final String REL_ZK_INTERCEPTOR_CONF = "zk-interceptor-conf";

	private final DOMRetriever xmlDoc;
	/**
	 * 
	 * @param xmlDoc
	 */
	XMLConfigurator(DOMRetriever xmlDoc){
		this.xmlDoc = xmlDoc;
	}
	/**
	 * 
	 * @param xmlTextContent
	 */
	public XMLConfigurator(String xmlTextContent){
		this.xmlDoc = new DOMRetriever(xmlTextContent);
	}
	/**
	 * 
	 */
	public void configure(final ZMonitorManager manager) {
		Node monitorMgmtNode = xmlDoc.getNodeList(ABS_PROFILING_MANAGER).item(0);
		prepareServletContainerConf(manager, xmlDoc, monitorMgmtNode);
		prepareCustomConfiguration(manager, xmlDoc, monitorMgmtNode);
		prepareMPointInfoFacotry(manager, xmlDoc, monitorMgmtNode);
		prepareMSquenceHandlers(manager, xmlDoc, monitorMgmtNode);
	}

	private static void prepareServletContainerConf( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		Node node = getSingleton(REL_SERVLET_CONTAINER_CONF, xmlDoc, monitorMgmtNode);
		if(node==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initCustomConfiguration(manager, xmlDoc, node, new JavaWebConfiguration(), true);
	}
	
	private static void prepareCustomConfiguration( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node currentNode){
		
		new NodeIterator<ZMonitorManager>() {
			protected void forEach(int index, final Node configNode, ZMonitorManager manager) {
				CustomConfiguration customConf = newInstanceByClassAttr(configNode, null, true);
				initCustomConfiguration(manager, xmlDoc, configNode, customConf, true);
			}
		}.iterate(xmlDoc.getNodeList(currentNode, REL_CUSTOM_CONFIGURATION), manager);
	}
	
	
	private static void prepareMPointInfoFacotry(ZMonitorManager manager, 
			DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		
		Node mpInfoFacNode = getSingleton(REL_MEASURE_POINT_INFO_FACTORY, xmlDoc, monitorMgmtNode);
		if(mpInfoFacNode==null)return;//use default...
		
		MonitorPointInfoFactory mpInfoFactory = newInstanceByClassAttr(mpInfoFacNode, null, true);
		applyPropertyTagsToBean(xmlDoc, mpInfoFacNode, new PropertySetter(mpInfoFactory));
		manager.setMonitorPointInfoFactory(mpInfoFactory);
	}
	
	
	private static void prepareMSquenceHandlers(final ZMonitorManager manager, 
			DOMRetriever xmlDoc, 
			Node profilerMgmtNode){
		
		new NodeIterator<DOMRetriever>() {
			protected void forEach(int index, Node node, DOMRetriever xmlDoc) {
				MonitorSequenceHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMRetriever.getAttributeValue(node, NAME);
				applyPropertyTagsToBean(xmlDoc, node, new PropertySetter(handler));
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				manager.addMonitorSequenceHandler(name, handler);
				if(handler instanceof CustomConfiguration){
					initCustomConfiguration(manager, xmlDoc, node, (CustomConfiguration) handler, false);
				}
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
			final DOMRetriever xmlDoc, 
			Node configNode, 
			CustomConfiguration customConf, 
			boolean shouldApplyProperties){
		
		if(shouldApplyProperties){
			applyPropertyTagsToBean(xmlDoc, configNode, new PropertySetter(customConf));
		}
		customConf.apply(manager, xmlDoc, configNode);
		manager.addCustomConfiguration(customConf);
	}
	/**
	 * 
	 * @param singletonXPath
	 * @param xmlDoc
	 * @param parentNode
	 * @return
	 */
	public static Node getSingleton(String singletonXPath, 
			DOMRetriever xmlDoc, 
			Node parentNode){
		
		NodeList nList = xmlDoc.getNodeList(parentNode, singletonXPath);
		int length = nList.getLength();
		if(length>1){
			throw new IllegalArgumentException(Strings.append("you got multiple ",singletonXPath,
					" in your " ,XmlConfiguratorLoader.ZMONITOR_XML,
					", which one is that you want? length=",length));
		}
		return nList.item(0);
	}
}
