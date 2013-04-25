/**
 * 
 */
package org.zkoss.monitor.agent;

import static org.zmonitor.impl.XMLConfigs.applyAttributesToBean;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.XmlConfiguratorLoader;
import org.zmonitor.util.DOMRetriever;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AgentManagerConfigXml_Snippet {

	public static final String REL_AGENT_CONF = "agent-conf";
	private static void prepareAgent( ZMonitorManager manager, 
			final DOMRetriever xmlDoc, 
			Node monitorMgmtNode){
		// init agent
		Node node = getSingleton(REL_AGENT_CONF, xmlDoc, monitorMgmtNode);
		if(node!=null){//init Master...
			Agent agent = new Agent();
			applyAttributesToBean( xmlDoc, node, new PropertySetter(agent), null);
//			initCustomConfiguration(manager, xmlDoc, node, agent, true);
//			manager.setAgent(agent);
		}
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
