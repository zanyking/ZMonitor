/**
 * 
 */
package org.zkoss.monitor.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;




/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class AsyncExecutor {
	private static final long DEFAULT_TIME_OUT = 5*60*1000L;
	
	private static final Terminator TERMINATOR = new Terminator( 1*60*1000L);
	protected static final ApplicationThreadFactory APP_THREAD_FACTORY = 
		new ApplicationThreadFactory(10);
	
	private final SingleBoundedExecutorService singleExecutor;
	private volatile Runnable operation;
	private volatile Future<?> future;
	
	public AsyncExecutor(){
		this(null);
	}
	public AsyncExecutor(Runnable runnable){
		singleExecutor = new SingleBoundedExecutorService( 1000L, APP_THREAD_FACTORY);
		this.operation = runnable;
	}
	
	public Runnable getOperation() {
		return operation;
	}

	/**
	 * trigger Async Execution, if it is already running, the operation will be ignored.   
	 */
	public synchronized void trigger(){
		if(isIdle()){
			future = singleExecutor.submit(new NamedRunnable() {
				public void run() {
					TERMINATOR.add(System.currentTimeMillis(), 
							DEFAULT_TIME_OUT, 
							Thread.currentThread());
					operation.run();
				}

				public String getName() {
					return "AsyncExecutor-thread";
				}
			});
			TERMINATOR.start();
		}
	}
	
	/**
	 * execute the assigned runnable directly.
	 */
	public void invoke(){
		trigger();
		while(true){
			try {// sleep a while.
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(isIdle()){
				break;
			}
		}
	}
	
	private synchronized boolean isIdle(){
		return future==null || future.isDone();
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	interface NamedRunnable extends Runnable{
		public String getName();
	}
	
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public final static class ApplicationThreadFactory implements ThreadFactory{
	    private final ThreadGroup group;
	    private final AtomicInteger threadNumber = new AtomicInteger(1);
	    private final AtomicInteger threadAmount = new AtomicInteger();
	    private int limit;

	    private ApplicationThreadFactory(int limit) {
	    	this.limit = limit;
	    	
	        SecurityManager s = System.getSecurityManager();
	        group = (s != null)? s.getThreadGroup() :
	                             Thread.currentThread().getThreadGroup();
	    }
	    public int getLimit() {
			return limit;
		}
	    
		public void setLimit(int limit) {
			this.limit = limit;
		}

		public Thread newThread(final Runnable r) {
	    	
	    	//test if current request exceeded the thread amount limitation.
	    	try {
				if(!shouldGive()){
					return null;
				}
			} catch (InterruptedException e) {
				//do nothing...
			}
			
			String threadName = "async-op-"+threadNumber.getAndIncrement();
			if(r instanceof NamedRunnable){
				threadName +=  "-"+((NamedRunnable)r).getName();
			}
			
	        Thread thread = new Thread(group, new Runnable(){
				public void run() {
					int currentAppThreadAmount = 0;
					try{
						currentAppThreadAmount = threadAmount.incrementAndGet();
//						ZMLog.debug("AsyncExecutor: Current App Thread Amount + : "+currentAppThreadAmount);
						r.run();
					}finally{
						currentAppThreadAmount = threadAmount.decrementAndGet();
//						ZMLog.debug("AsyncExecutor: Current App Thread Amount - : "+currentAppThreadAmount);
					}
				}},
				threadName, 0);
	        if (thread.isDaemon())
	            thread.setDaemon(false);
	        if (thread.getPriority() != Thread.NORM_PRIORITY)
	            thread.setPriority(Thread.NORM_PRIORITY);
	        return thread;
	    }
	    
	    private boolean shouldGive() throws InterruptedException{
	    	if(threadAmount.get()<limit)return true;
	    	for(int count=10 ; count>0 ; count--){
	    		Thread.sleep(500);
	    		if(threadAmount.get()<limit)return true;
	    	}
	    	return false;
	    }
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class Terminator{
		/**
		 * @author Ian YT Tsai(Zanyking)
		 */
		private class Termination{
			final long startTime;
			final long timeout;
			final Thread thread;
			public Termination(long startTime, long timout, Thread thread) {
				super();
				this.startTime = startTime;
				this.timeout = timout;
				this.thread = thread;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((thread == null) ? 0 : thread.hashCode());
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
				Termination other = (Termination) obj;
				if (thread == null) {
					if (other.thread != null)
						return false;
				} else if (!thread.equals(other.thread))
					return false;
				return true;
			}
		}//end of class...
		private final SingleBoundedExecutorService singleExecutor;
		private final Set<Termination> store;
		private final AtomicInteger threadNumber = new AtomicInteger();
		/**
		 * 
		 * @param capacity
		 */
		public Terminator( long aliveTime) {
			singleExecutor = new SingleBoundedExecutorService( aliveTime, new ThreadFactory() {
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setName("async_op_terminator-"+threadNumber.incrementAndGet());
			        if (t.isDaemon())
			            t.setDaemon(false);
			        if (t.getPriority() != Thread.NORM_PRIORITY)
			            t.setPriority(Thread.NORM_PRIORITY);
			        return t;
				}
			});
			store = Collections.synchronizedSet(new HashSet<Termination>()); 
		}

		public void add(long startTime, long timeout, Thread thread){
			store.add(new Termination(startTime, timeout, thread));
		}
		
		public void start(){
			try{
				singleExecutor.submit(new Runnable() {
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							return;// maybe next time...
						}
						
						ArrayList<Termination> arr = new  ArrayList<Termination>(store);
						int counter = 0;
						for(Termination termination : arr){
							if(!termination.thread.isAlive()){
								store.remove(termination);
								continue;
							}
							boolean isTerminated = handle(termination);
							if(isTerminated)counter++;
						}
//						ZMLog.debug("AsyncExecutor: Terminated threads count:"+counter);//TODO: change this line to log!
					}
				});	
			}catch(RejectedExecutionException e){
				// Do nothing, it happened just because the singleExecutor is occupied.
			}
		}
		
		private boolean handle(Termination termination){
			long current = System.currentTimeMillis();
			long duration = current - termination.startTime;
			if(duration >= termination.timeout){
				try {
//					ZMLog.info("AsyncExecutor: this thread: " +termination.thread.getName()
//							+" takes too much time:[" + duration  
//									+ "]ms interrupt it");//TODO: change this line to log!
					termination.thread.interrupt();
					store.remove(termination);
				} catch (Exception e) {
					//ignore, because no matter what this thread
				}
				return true;
			}
			return false;
		}
		
	}//end of class...
	
	/**
	 * 
	 * @param limit
	 */
	public static void setThreadFactoryLimit(int limit){
		APP_THREAD_FACTORY.setLimit(limit);
	}
	
//	public static void main(String[] args) throws Exception{
//		AsyncExecutor[] opArr = new AsyncExecutor[]{
//				new AsyncExecutor("xop1", new AsyncOperation() {
//					public void run(OperationContext opContext) throws Exception {
//						Thread.sleep(15000L);
//						System.out.println("finished 1!!");
//					}
//				}),
//				new AsyncExecutor("xop2", new AsyncOperation() {
//					public void run(OperationContext opContext) throws Exception {
//						Thread.sleep(1000L);
//						System.out.println("finished 2!!");
//					}
//				}),
//				new AsyncExecutor("xop3", new AsyncOperation() {
//					public void run(OperationContext opContext) throws Exception {
//						Thread.sleep(1000L);
//						System.out.println("finished 3!!");
//					}
//				}),
//				new AsyncExecutor("xop4", new AsyncOperation() {
//					public void run(OperationContext opContext) throws Exception {
//						Thread.sleep(1000L);
//						System.out.println("finished 4!!");
//					}
//				})
//		};
//		Thread.sleep(15*1000);
//		for (int i = 0; i < opArr.length; i++) {
//			System.out.println();
//			System.out.println(">>>> OperationContext["+i+"]: isDone="+opArr[i].isDone());
//			System.out.println(">>>> OperationContext["+i+"]: isProcessing="+opArr[i].isProcessing());
//			OperationContext opCtx = opArr[i].getOperationContext();
//			if(opCtx.getThrowable()!=null){
//				System.out.println(">>>> OperationContext["+i+"]: error="+opCtx.getThrowable());
//			}
//		}
//	}
}
