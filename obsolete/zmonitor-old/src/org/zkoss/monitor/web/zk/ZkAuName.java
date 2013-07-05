/**ZkAuName.java
 * 2011/3/17
 * 
 */
package org.zkoss.monitor.web.zk;

import java.io.Serializable;

import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.util.ArrayBag;
import org.zkoss.monitor.util.Strings;
import org.zkoss.zk.ui.Component;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZkAuName implements Name, Serializable{
	private static final long serialVersionUID = 2385793506975290788L;
	private final String type;
	private final ArrayBag<String> eventBag;
	
	public ZkAuName(String type) {
		this.type = type;
		this.eventBag = new ArrayBag<String>();
	}

	public String getType() {
		return type;
	}
	
	public void add(String command, Component comp){
		if(comp==null){
			eventBag.add(command);
		}else{
			eventBag.add(Strings.append(
					command," ",comp.getId()," ", comp.getClass().getSimpleName()));
		}
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventBag == null) ? 0 : eventBag.hashCode());
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
		ZkAuName other = (ZkAuName) obj;
		if (eventBag == null) {
			if (other.eventBag != null)
				return false;
		} else if (!eventBag.equals(other.eventBag))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Strings.append(sb, type);
		for(String str : eventBag){
			Strings.append(sb, " ", str);
		}
		return sb.toString();
	}
	
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		for(String str : eventBag){
			Strings.append(sb, " ", str);
		}
		return sb.toString();
	}
}
