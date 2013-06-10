/**CoreConfigurator.java
 * 2011/4/3
 * 
 */
package org.zmonitor.impl;

import static org.zmonitor.impl.XMLConfigs.NAME;
import static org.zmonitor.impl.XMLConfigs.newInstanceByClassAttr;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.ConfigContext.Visitor;
import org.zmonitor.config.Configs;
import org.zmonitor.config.WrongConfigurationException;
import org.zmonitor.impl.MSPipe.Mode;
import org.zmonitor.spi.Configurator;
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
	
	
	public static final String REL_TIMELINE_HANDLER = "monitor-sequence-handler";
	private static final String REL_MONITOR_SEQUENCE_PIPE = "monitor-sequence-pipe";
	
	
	public void configure(ConfigContext monitorMgmt) {
		prepareMSPipe(monitorMgmt);
		prepareMSHandlers(monitorMgmt);
	}

	private static void prepareMSPipe(ConfigContext  monitorMgmt){
		//TODO: make mode configurable...
		ConfigContext msPipeCtx = monitorMgmt.toNode(REL_MONITOR_SEQUENCE_PIPE);
		if(msPipeCtx.getNode()==null){
			MSPipe pipe = MSPipeProvider.getPipe(Mode.SYNC);
			Configs.initCustomConfigurable(msPipeCtx, pipe, false);
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
	
	private static void prepareMSHandlers(ConfigContext  configCtx){
		configCtx.forEach(REL_TIMELINE_HANDLER, new Visitor(){
			public boolean visit(int index, ConfigContext nodeCtx) {
				MonitorSequenceHandler handler = nodeCtx.newBean(null, true);
				PropertySetter setter = new PropertySetter(handler);
//				nodeCtx.applyPropertyTags(setter);
				nodeCtx.applyAttributes(setter, "name", "class");
				
				String name = DOMs.getAttributeValue(nodeCtx.getNode(), NAME);
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				handler.setId(name);
				
				nodeCtx.getManager().addMonitorSequenceHandler(handler);
				if(handler instanceof CustomConfigurable){
					Configs.initCustomConfigurable(nodeCtx, (CustomConfigurable) handler, true);
				}
				return false;
			}
		});
		
	}
	

}
