/**CoreConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.zmonitor.WrongConfigurationException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.spi.XMLConfiguration.Visitor;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.DOMs;
import org.zmonitor.util.PropertySetter;
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
	public void configure(final ZMonitorManager manager,  XMLConfiguration ctxt) {
		Node monitorMgmtNode = ctxt.getNode(ABS_PROFILING_MANAGER, null);
		prepareWebConf(manager, ctxt, monitorMgmtNode);
		prepareMPointInfoFacotry(manager, ctxt, monitorMgmtNode);
		prepareMSHandlers(manager, ctxt, monitorMgmtNode);
	}

	private static void prepareWebConf( ZMonitorManager manager, 
			final XMLConfiguration ctxt, 
			Node monitorMgmtNode){
		Node node = ctxt.getNode(REL_SERVLET_CONTAINER_CONF, monitorMgmtNode);
		if(node==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initCustomConfiguration(manager, ctxt, node, new JavaWebConfiguration(), true);
	}
	
	
	
	private static void prepareMPointInfoFacotry(ZMonitorManager manager, 
			XMLConfiguration config, 
			Node monitorMgmtNode){
		
		Node mpInfoFacNode = config.getNode(REL_MEASURE_POINT_INFO_FACTORY, monitorMgmtNode);
		if(mpInfoFacNode==null)return;//use default...
		
		MonitorPointInfoFactory mpInfoFactory = newInstanceByClassAttr(mpInfoFacNode, null, true);
		
		config.applyPropertyTagsToBean( mpInfoFacNode, new PropertySetter(mpInfoFactory));
		manager.setMonitorPointInfoFactory(mpInfoFactory);
	}
	
	
	private static void prepareMSHandlers(final ZMonitorManager manager, 
			final XMLConfiguration config, 
			Node profilerMgmtNode){
		config.forEach(profilerMgmtNode, REL_TIMELINE_HANDLER, new Visitor(){
			public boolean visit(int index, Node node) {
				MonitorSequenceHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMs.getAttributeValue(node, NAME);
				config.applyPropertyTagsToBean( node, new PropertySetter(handler));
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				manager.addMonitorSequenceHandler(name, handler);
				if(handler instanceof CustomConfiguration){
					initCustomConfiguration(manager, config, node, (CustomConfiguration) handler, false);
				}
				return false;
			}
		});
		
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
			final XMLConfiguration config, 
			Node configNode, 
			CustomConfiguration customConf, 
			boolean shouldApplyProperties){
		if(shouldApplyProperties){
			config.applyPropertyTagsToBean( configNode, new PropertySetter(customConf));
		}
		customConf.apply(manager, config, configNode);
		manager.addCustomConfiguration(customConf);
	}

}
