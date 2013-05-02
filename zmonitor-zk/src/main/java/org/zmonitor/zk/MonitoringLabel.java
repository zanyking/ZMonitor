/**
 * 
 */
package org.zmonitor.zk;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Label;
import org.zmonitor.ZMonitor;
import org.zmonitor.impl.StringName;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 * 
 * <pflabel message="any message you want!"/>
 */
public class MonitoringLabel extends Label implements AfterCompose {

	private static final long serialVersionUID = -4660284625138540316L;
	private String message;
	/**
	 * @return the prefix
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public void afterCompose() {
		ZMonitor.record(new StringName("ZUL_TAG"), message);
		this.detach();
	}

}
