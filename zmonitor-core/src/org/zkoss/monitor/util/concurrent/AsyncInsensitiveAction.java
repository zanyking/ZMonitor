/**
 * 
 */
package org.zkoss.monitor.util.concurrent;


/**
 * 1. Allowed only one action thread working at the same time.<br>
 * 2. Calling {@link AsyncInsensitiveAction#trigger()} will has no effect if there's already an up running action thread.<br>
 * 3. Calling {@link AsyncInsensitiveAction#invoke()} will block current thread till the action thread is finished.
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public abstract class AsyncInsensitiveAction {
	
	
	protected long waitMillis;
	protected final AsyncExecutor consumeAExec;
	protected final AsyncExecutor waitForGroupingAExec;
	private volatile boolean hasSlept;
	/**
	 * 
	 */
	public AsyncInsensitiveAction(){
		this(-1l);
	}
	/**
	 * 
	 * @param waitMillis set it to negative (ex: -1) to disable it. 
	 */
	public AsyncInsensitiveAction(final long waitMillis) {
		super();
		this.waitMillis = waitMillis;
		 consumeAExec = new AsyncExecutor(getAction());
		 
		 waitForGroupingAExec = new AsyncExecutor(new Runnable(){
				public void run() {
					if(!reachThreshold()){
						try {
							while(!hasSlept){
								Thread.sleep(AsyncInsensitiveAction.this.waitMillis);
								hasSlept = true;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					consumeAExec.trigger();
				}});
	}
	/**
	 * a non-blocking method which will start the action thread if conditions allowed.
	 * will has no effect if there's already an working action thread. 
	 */
	public void trigger(){
		if(reachThreshold()){
			consumeAExec.trigger();
		}else if(waitMillis > 0 ){
			hasSlept = false;
			waitForGroupingAExec.trigger();	
		}
	}
	/**
	 * a blocking method which will start the action thread and wait till it's finished.<br>
	 * If there's already a working thread, this method will simply wait for the it's finish.
	 */
	public void invoke(){
		consumeAExec.invoke();
	}
	/**
	 * 
	 * @return
	 */
	public long getWaitMillis() {
		return waitMillis;
	}
	/**
	 * 
	 * @param waitMillis
	 */
	public void setWaitMillis(long waitMillis) {
		this.waitMillis = waitMillis;
	}
	
	/**
	 * 
	 * @return the action that you want to make it "Insensitive".
	 */
	protected abstract Runnable getAction();
	
	/**
	 * implement your own threshold detection implementation.<br>
	 * need to be implemented in a thread safe manner.<br>
	 * 
	 * @return if reaches the Threshold
	 */
	protected boolean reachThreshold(){
		return true;
	}
	
	
}
