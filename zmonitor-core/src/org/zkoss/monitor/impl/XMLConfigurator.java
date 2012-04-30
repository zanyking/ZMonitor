/**XmlProfilerConfigurator.java
 * 2011/4/3
 * 
 */
package org.zkoss.monitor.impl;

import static org.zkoss.monitor.impl.XMLConfigs.NAME;
import static org.zkoss.monitor.impl.XMLConfigs.applyAttributesToBean;
import static org.zkoss.monitor.impl.XMLConfigs.applyPropertyTagsToBean;
import static org.zkoss.monitor.impl.XMLConfigs.newInstance;
import static org.zkoss.monitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zkoss.monitor.Agent;
import org.zkoss.monitor.WrongConfigurationException;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.spi.Configurator;
import org.zkoss.monitor.spi.CustomConfiguration;
import org.zkoss.monitor.spi.IdFetcher;
import org.zkoss.monitor.spi.MeasurePointInfoFactory;
import org.zkoss.monitor.spi.TimelineHandler;
import org.zkoss.monitor.util.DOMRetriever;
import org.zkoss.monitor.util.NodeIterator;
import org.zkoss.monitor.util.PropertySetter;
import org.zkoss.monitor.util.Strings;
import org.zkoss.monitor.web.JavaWebConfiguration;
import org.zkoss.monitor.web.zk.ZKInterceptorConfiguration;

/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class XMLConfigurator implements Configurator {
	
	private static final String ABS_PROFILING_MANAGER = "/zmonitor";
	
	public static final String REL_AGENT_CONF = "agent-conf";
	
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
		prepareAgent(manager, xmlDoc, monitorMgmtNode);
		prepareUuid(manager, xmlDoc, monitorMgmtNode);
		prepareServletContainerConf(manager, xmlDoc, monitorMgmtNode);
		prepareZKInterceptorConf(manager, xmlDoc, monitorMgmtNode);
		prepareCustomConfiguration(manager, xmlDoc, monitorMgmtNode);
		prepareMeasurePointInfoFacotry(manager, xmlDoc, monitorMgmtNode);
		prepareTimelineHandlers(manager, xmlDoc, monitorMgmtNode);
		
	}
	private static void prepareAgent( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		// init agent
		Node node = getSingleton(REL_AGENT_CONF, xmlDoc, monitorMgmtNode);
		if(node!=null){//init Master...
			Agent agent = new Agent();
			applyAttributesToBean( xmlDoc, node, new PropertySetter(agent), null);
			initCustomConfiguration(manager, xmlDoc, node, agent, true);
			manager.setAgent(agent);
		}
	}
	private static void prepareUuid( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		String uuid = manager.getUuid();
		if(uuid!=null){
			ZMLog.debug("ZMonitor manual uuid is:"+uuid);
			return;
		}
		ZMonitorSelf self = new ZMonitorSelf();
		applyAttributesToBean( xmlDoc, monitorMgmtNode, new PropertySetter(self), null);
		if(self.getUuid()!=null){
			uuid = self.getUuid();
		}else if(self.getIdFetcherClass()!=null){
			IdFetcher aIdFetcher = newInstance(self.getIdFetcherClass(), null);
			uuid = aIdFetcher.fetch(manager);
		}
		ZMLog.debug("current ZMonitor instance uuid is:"+uuid);
		manager.setUuid(uuid);
	}
	
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class ZMonitorSelf{
		private String uuid;
		private String idFetcherClass;
		public String getUuid() {
			return uuid;
		}
		public void setUuid(String id) {
			this.uuid = id;
		}
		public String getIdFetcherClass() {
			return idFetcherClass;
		}
		public void setIdFetcherClass(String idFetcherClass) {
			this.idFetcherClass = idFetcherClass;
		}
	}//end of class...
	
	
	
	private static void prepareServletContainerConf( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		Node node = getSingleton(REL_SERVLET_CONTAINER_CONF, xmlDoc, monitorMgmtNode);
		if(node==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initCustomConfiguration(manager, xmlDoc, node, new JavaWebConfiguration(), true);
	}
	private static void prepareZKInterceptorConf( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		
		Node node = getSingleton(REL_SERVLET_CONTAINER_CONF, xmlDoc, monitorMgmtNode);
		if(node==null)return;//use default...
		
		initCustomConfiguration(manager, xmlDoc, node, new ZKInterceptorConfiguration(), true);
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
	
	
	private static void prepareMeasurePointInfoFacotry(ZMonitorManager manager, 
			DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		
		Node mpInfoFacNode = getSingleton(REL_MEASURE_POINT_INFO_FACTORY, xmlDoc, monitorMgmtNode);
		if(mpInfoFacNode==null)return;//use default...
		
		MeasurePointInfoFactory mpInfoFactory = newInstanceByClassAttr(mpInfoFacNode, null, true);
		applyPropertyTagsToBean(xmlDoc, mpInfoFacNode, new PropertySetter(mpInfoFactory));
		manager.setMeasurePointInfoFactory(mpInfoFactory);
	}
	
	
	private static void prepareTimelineHandlers(final ZMonitorManager manager, 
			DOMRetriever xmlDoc, 
			Node profilerMgmtNode){
		
		new NodeIterator<DOMRetriever>() {
			protected void forEach(int index, Node node, DOMRetriever xmlDoc) {
				TimelineHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMRetriever.getAttributeValue(node, NAME);
				applyPropertyTagsToBean(xmlDoc, node, new PropertySetter(handler));
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				manager.addTimelineHandler(name, handler);
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
