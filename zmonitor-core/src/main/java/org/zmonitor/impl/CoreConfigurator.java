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
	
	private static void prepareMSHandlers(final ConfigContext  configCtx){
		configCtx.forEach(REL_TIMELINE_HANDLER, new Visitor(){
			public boolean visit(int index, Node node) {
				MonitorSequenceHandler handler = newInstanceByClassAttr(node, null, true);
				String name = DOMs.getAttributeValue(node, NAME);
				configCtx.applyPropertyTags( new PropertySetter(handler));
				
				XMLConfigs.applyAttributesToBean(node, 
						new PropertySetter(handler), 
						XMLConfigs.ignores("name", "class"));
				
				if(name==null||name.length()<=0)
					throw new WrongConfigurationException(
							"You forgot to assign a \"name\" attribute to handler the declaration of: "+handler);
				handler.setId(name);
				
				configCtx.getManager().addMonitorSequenceHandler(handler);
				if(handler instanceof CustomConfigurable){
					Configs.initCustomConfigurable(configCtx, (CustomConfigurable) handler, false);
				}
				return false;
			}
		});
		
	}
	

}
