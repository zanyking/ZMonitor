/**
 * 
 */
package org.zkoss.monitor.agent;

import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;

import org.w3c.dom.Node;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.util.PropertySetter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AgentManagerConfigXml_Snippet {

	public static final String REL_AGENT_CONF = "agent-conf";
	private static void prepareAgent( ZMonitorManager manager, 
			final XMLConfiguration config, 
			Node monitorMgmtNode){
		// init agent
		Node node = config.getNode(REL_AGENT_CONF, monitorMgmtNode);
		if(node!=null){//init Master...
			Agent agent = new Agent();
			applyAttributesToBean( node, new PropertySetter(agent), null);
//			initCustomConfiguration(manager, xmlDoc, node, agent, true);
//			manager.setAgent(agent);
		}
	}
	
}
