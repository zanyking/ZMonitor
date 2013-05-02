/**
 * 
 */
package org.zkoss.monitor.agent;

import org.zmonitor.config.ConfigContext;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AgentManagerConfigXml_Snippet {

	public static final String REL_AGENT_CONF = "agent-conf";
	private static void prepareAgent(ConfigContext monitorMgmt){
		// init agent
		ConfigContext agentConf = monitorMgmt.toNode(REL_AGENT_CONF);
		
		if(agentConf.getNode()!=null){//init Master...
			Agent agent = new Agent();
			agentConf.applyAttributes(new PropertySetter(agent));
//			initCustomConfiguration(manager, xmlDoc, node, agent, true);
//			manager.setAgent(agent);
		}
	}
	
}
