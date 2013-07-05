/**AbstractName.java
 * 2011/3/17
 * 
 */
package org.zkoss.monitor.impl;

import java.io.Serializable;

import org.zkoss.monitor.spi.Name;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class StringName implements Name, Serializable{

	private static final long serialVersionUID = -1332409860773163992L;
	private final String type;
	private final String suffix;
	
	public StringName(String type) {
		this(type, "");
	}
	/**
	 * 
	 * @param name
	 */
	public StringName(String type, String suffix) {
		this.type = type;
		this.suffix = suffix;
	}

	public String getType() {
		return type;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
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
		StringName other = (StringName) obj;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String toString(){
		if(suffix==null || suffix.length()<=0)return type;
		return type+" "+suffix;
	}
	public String toShortString() {
		return suffix==null?type:suffix;
	}
}
