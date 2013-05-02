/**CoreConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.zmonitor.CustomConfigurable;
import org.zmonitor.config.WrongConfigurationException;
import org.zmonitor.spi.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.spi.XMLConfiguration.Visitor;
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
	
	public static final String REL_WEB_CONF = "web-conf";
	
	public void configure(ConfigContext configCtx) {
		ConfigContext  monitorMgmt = configCtx.query(ABS_PROFILING_MANAGER);
		
		prepareWebConf(monitorMgmt);
		prepareMPointInfoFacotry(monitorMgmt);
		prepareMSHandlers(monitorMgmt);
	}

	private static void prepareWebConf(ConfigContext  monitorMgmt){
		ConfigContext webConf = monitorMgmt.query(REL_WEB_CONF);
		
		if(webConf.getNode()==null)return;//use default...
		
		//TODO: Be careful of introducing sub packages stuff inside, it might not work.  
		initCustomConfiguration(webConf, new JavaWebConfiguration(), true);
	}
	
		
	
	private static void prepareMPointInfoFacotry(ConfigContext  monitorMgmt){
		
		ConfigContext mpInfoFac = monitorMgmt.query(REL_MEASURE_POINT_INFO_FACTORY);
		if(mpInfoFac.getNode()==null)return;//use default...
		
		MonitorPointInfoFactory mpInfoFactory = mpInfoFac.newBean(null, true);
		mpInfoFac.applyPropertyTags( new PropertySetter(mpInfoFactory));
		mpInfoFac.getManager().setMonitorPointInfoFactory(mpInfoFactory);
	}
	
	
	private static void prepareMSHandlers(final ConfigContext  monitorMgmt){
		monitorMgmt.forEach(REL_TIMELINE_HANDLER, new Visitor(){
			public boolean visit(int index, Node node) {
				MonitorSequenceHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMs.getAttributeValue(node, NAME);
				monitorMgmt.applyPropertyTags( new PropertySetter(handler));
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				handler.setId(name);
				monitorMgmt.getManager().addMonitorSequenceHandler(handler);
				if(handler instanceof CustomConfigurable){
					initCustomConfiguration(monitorMgmt, (CustomConfigurable) handler, false);
				}
				return false;
			}
		});
		
	}
	/**
	 * 
	 * @param configCtx
	 * @param customConf
	 * @param shouldApplyProperties
	 */
	public static void initCustomConfiguration(ConfigContext configCtx, 
			CustomConfigurable customConf, 
			boolean shouldApplyProperties){
		if(shouldApplyProperties){
			configCtx.applyPropertyTags(new PropertySetter(customConf));
		}
		customConf.configure(configCtx);
		//TODO: use ZMBean instead.
		configCtx.getManager().addCustomConfiguration(customConf);
	}

}
