/**
 * 
 */
package org.zmonitor.bean;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ZMBeanBase extends LifeCycleBase implements ZMBean {

	protected String id;
	
	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}
	@Override
	protected void doStart() {
		//DO NOTHING...
	}

	@Override
	protected void doStop() {
		//DO NOTHING...
	}
}
