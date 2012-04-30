/**ZkEventName.java
 * 2011/4/11
 * 
 */
package org.zkoss.monitor.web.zk;

import java.io.Serializable;

import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.util.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZkEventName implements Name, Serializable{

	private static final long serialVersionUID = 8152645005744893807L;
	protected String type;
	protected String eventName;
	protected String componentId;
	protected String componentType;

	/**
	 * 
	 * @param type
	 * @param eventName
	 * @param componentId
	 * @param componentType
	 */
	public ZkEventName(String type, String eventName, String componentId,
			String componentType) {
		super();
		this.type = type;
		this.eventName = eventName;
		this.componentId = componentId;
		this.componentType = componentType;
	}
	/**
	 * 
	 * @param eventName
	 * @param componentId
	 * @param componentType
	 */
	public ZkEventName(String eventName, String componentId, String componentType) {
		this("EVENT", eventName, componentId, componentType);
	}
	/**
	 * 
	 * @param type
	 * @param event
	 */
	public ZkEventName(String type, Event event) {
		this.type = type;
		this.eventName = event.getName();
		Component component = event.getTarget();
		this.componentId = component.getId();
		this.componentType = component.getClass().getSimpleName();
	}
	/**
	 * 
	 * @param event
	 */
	public ZkEventName(Event event){
		this("EVENT", event);
	}
	
	public String toString(){
		return Strings.append(type, "<", componentType, " ", componentId, eventName,">");
	}
	public String toShortString() {
		return Strings.append("<", componentType, " ", componentId, eventName,">");
	}
	
	public String getType() {
		return type;
	}
	public String getEventName() {
		return eventName;
	}
	public String getComponentId() {
		return componentId;
	}
	public String getComponentType() {
		return componentType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((componentId == null) ? 0 : componentId.hashCode());
		result = prime * result
				+ ((componentType == null) ? 0 : componentType.hashCode());
		result = prime * result
				+ ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZkEventName other = (ZkEventName) obj;
		if (componentId == null) {
			if (other.componentId != null)
				return false;
		} else if (!componentId.equals(other.componentId))
			return false;
		if (componentType == null) {
			if (other.componentType != null)
				return false;
		} else if (!componentType.equals(other.componentType))
			return false;
		if (eventName == null) {
			if (other.eventName != null)
				return false;
		} else if (!eventName.equals(other.eventName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
