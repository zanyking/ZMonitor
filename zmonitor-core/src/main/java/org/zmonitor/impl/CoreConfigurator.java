/**CoreConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.w3c.dom.Node;
import org.zmonitor.CustomConfigurable;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.ConfigContext.Visitor;
import org.zmonitor.config.Configs;
import org.zmonitor.config.WrongConfigurationException;
import org.zmonitor.impl.MSPipeProvider.MSPipe;
import org.zmonitor.impl.MSPipeProvider.Mode;
import org.zmonitor.spi.Configurator;
import org.zmonitor.spi.MonitorPointInfoFactory;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.DOMs;
import org.zmonitor.util.PropertySetter;

/**
 * 
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class CoreConfigurator  implements Configurator {
	
	
	public static final String REL_MEASURE_POINT_INFO_FACTORY = "mp-info-factory";
	public static final String REL_TIMELINE_HANDLER = "monitor-sequence-handler";
	private static final String REL_MONITOR_SEQUENCE_PIPE = "monitor-sequence-pipe";
	
	
	public void configure(ConfigContext monitorMgmt) {
		prepareMSPipe(monitorMgmt);
		prepareMPointInfoFacotry(monitorMgmt);
		prepareMSHandlers(monitorMgmt);
	}

	private static void prepareMSPipe(ConfigContext  monitorMgmt){
		//TODO: make mode configurable...
		ConfigContext msPipeCtx = monitorMgmt.toNode(REL_MONITOR_SEQUENCE_PIPE);
		if(msPipeCtx.getNode()==null){
			MSPipe pipe = MSPipeProvider.getPipe(Mode.SYNC);
			monitorMgmt.getManager().setMSPipe(pipe);
			return;//use default...
		}
		
		MSPipe pipe = newInstanceByClassAttr(msPipeCtx.getNode(), null, false);
		if(pipe==null){
			pipe = MSPipeProvider.getPipe(msPipeCtx.getAttribute("mode")); 
		}		
		msPipeCtx.applyAttributes(new PropertySetter(pipe), "mode", "class");
		Configs.initCustomConfigurable(msPipeCtx, pipe, false);
		msPipeCtx.getManager().setMSPipe(pipe);
	}
	
	private static void prepareMPointInfoFacotry(ConfigContext  monitorMgmt){
		
		ConfigContext mpInfoFac = monitorMgmt.toNode(REL_MEASURE_POINT_INFO_FACTORY);
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
					Configs.initCustomConfigurable(monitorMgmt, (CustomConfigurable) handler, false);
				}
				return false;
			}
		});
		
	}
	

}
