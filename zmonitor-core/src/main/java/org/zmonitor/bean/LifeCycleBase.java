/**
 * 
 */
package org.zmonitor.bean;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class LifeCycleBase implements LifeCycle {
	private static final int INIT = 0;
	private static final int STARTED = 1;
	private static final int STOPPED = -1;
	protected volatile int status;// 0 constructed, 1 started, -1 stopped

	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#start()
	 */
	public void start() {
		if(status!=INIT)
			throw new IllegalStateException(status==STARTED?
					"already started": "cannot start a stopped object");
		doStart();//prepare context...
		status = STARTED;
	}
	/**
	 * 
	 */
	protected abstract void doStart();

	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#stop()
	 */
	public void stop() {
		if(status!=STARTED)
			throw new IllegalStateException(status==INIT?
					"only a started bean can be stopped" : "already stopped");
		
		doStop();// release resources...
		status = STOPPED;
	}
	/**
	 * 
	 */
	protected abstract void doStop();

	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#isStarted()
	 */
	public boolean isStarted() {
		return status==STARTED;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.bean.ZMBean#isStopped()
	 */
	public boolean isStopped() {
		return status==STOPPED;
	}

}
