/**
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.IOException;

import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.CoreTrackingContext;
import org.zmonitor.impl.SimpleMonitorMeta;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.marker.Marker;
import org.zmonitor.marker.MarkerFactory;
import org.zmonitor.spi.MonitorLifecycle;


/**
 * <p>
 * This is an utility class to record {@link MonitorPoint} of current (a.k.a {@link MonitorSequence}).<br>
 * About {@link MonitorSequence}, any program has it, for example: 
 * <ul>
 *    <li>A user send a request to your Web Application, and the servlet's doPost() was called.</li>
 *    <li>A user clicked a button on your App's UI, which triggered a business logic with multiple classes method call involved.</li>
 * </ul>  
 *  The whole concept of this model is based on Log4J's Nested Diagnostic Context, you can use this NDC to verify:   
 * <ul>
 *   <li>How long will it take from measuring point A to measuring point B?</li>
 *   <li>Which step(MP i to MP i+1) takes the longest period in the execution?</li>
 *   <li>What's the current context of class: A method: B ? how many distinct possible context will come to this point?</li>
 * </ul> 
 * </p>
 * 
 *@author Ian YT Tsai(Zanyking)
 */
public final class ZMonitor {
	
	private static final Marker NATIVE_MARKER = MarkerFactory.getMarker("native"); 
	private static final String DEFAULT_TRACKER_NAME = "default";
	
	private ZMonitor(){}
	/*
	 * the ZMonitor self ignition is designed for lazy developer who want to use ZMonitor API directly without
	 * further programming of ZMonitorManager's initialization:
	 * 1. this process will search configuration file from:
	 * 	1.1. classpath:zmonitor.xml
	 *  1.2. specific URL assignment: -Dzmonitor.config.url=xxx.ooo
	 * 2. ZMonitor will stay in NOOP mode(no operation) if no configuration file has been found.
	 *   
	 */
	static{ 
		if(!ZMonitorManager.isInitialized()){
			try {

				ZMonitorManager aZMonitorManager = new ZMonitorManager();
				
				//TODO: get Configuration Source...
				ConfigSource confSrc = ConfigSources.loadForSimpleJavaProgram();
				if(confSrc==null){
					ZMLog.warn("The Configuration file["+
							ConfigSource.ZMONITOR_XML+"] neither assigned through System property nor exist under classpath" +
									", abort initialization.");
				}else{
					aZMonitorManager.performConfiguration(confSrc);	
					aZMonitorManager.setLifecycleManager(
							new ThreadLocalMonitorLifecycleManager());
						
					ZMonitorManager.init(aZMonitorManager);// might through AlreadyStartedExec...
					ZMLog.info("Init ZMonitor in pure Java mode: " + ZMonitor.class);
				}
				
			} catch (IOException e) {
				throw new InitFailureException(e);
			} catch (AlreadyStartedException e) {
				// Do nothing or simply log...
				ZMLog.info("already initialized by others");
			}
		}
		
	}
	

	
	private static MonitorLifecycle getLifecycle(){
		return ZMonitorManager.getInstance().getMonitorLifecycle();
	}
	
	/* **********************************
	 * Profilers utility methods.
	 * **********************************/
	
	/**
	 * @return true if there's a monitor sequence instance and which is started.
	 */
	public static boolean isMonitoring(){
		return getLifecycle().isMonitorStarted();
	}
	
	/**
	 * 
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint push(Object mesg){
		return push(NATIVE_MARKER, mesg, false);
	}	
	/**
	 * If you want to start a {@link MonitorPoint} in your Java code manually, use this method.<br>
	 * <p>
	 * This method will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack.<br>
	 * So this method will be cause some performance overhead to your program, if you are able to compose a {@link Name} by yourself, please use {@link #push(Name, Object)} instead.
	 * </p>
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @param traceCallerStack
	 * @return A {@link MonitorPoint} that will contain caller's java source information.
	 * @see #push(Name, Object)
	 */
	public static MonitorPoint push(Object mesg, boolean traceCallerStack) {
		return push(NATIVE_MARKER, mesg, traceCallerStack);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint push(Marker marker, Object message, boolean traceCallerStack){
		CoreTrackingContext ctx = new CoreTrackingContext(DEFAULT_TRACKER_NAME);
		ctx.setCreateMillis(System.currentTimeMillis());
		ctx.setMessage(message);
		ctx.setMonitorMeta(newMonitorMeta(marker, traceCallerStack, 3));
		return push(ctx);
	}
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint push(TrackingContext ctx){
		long nanosec = System.nanoTime();
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
		MonitorSequence ms = lc.getInstance();
		MonitorPoint mp = ms.start(ctx);
		ms.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		return mp;
	}
	
	
	
	private static MonitorMeta newMonitorMeta(Marker marker, boolean shouldDo, int callerLevel){
		if(!shouldDo)return null;
		StackTraceElement[] stackElemts = Thread.currentThread().getStackTrace();
		callerLevel += 1;
		if(stackElemts.length<=callerLevel){
			//this should never happened...
			throw new Error("How could method has no caller?");
		}
		StackTraceElement sElemt = stackElemts[callerLevel];
		return new SimpleMonitorMeta(marker, sElemt);
	}
	
	/**
	 *
	 * If you want to give a monitor point in your Java code manually, use this method.<br>
	 * It will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack. 
	 * 
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @return a {@link MonitorPoint} that will contain caller's java source information. 
	 */
	public static MonitorPoint record(Object mesg, boolean traceCallerStack){
		return record(null, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint record(String mesg){
		return record(null, mesg, false);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint record(Marker marker, Object message, boolean traceCallerStack){
		CoreTrackingContext ctx = new CoreTrackingContext(DEFAULT_TRACKER_NAME);
		ctx.setCreateMillis(System.currentTimeMillis());
		ctx.setMessage(message);
		ctx.setMonitorMeta(newMonitorMeta(marker, traceCallerStack, 3));
		return record(ctx);
	}
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint record(TrackingContext ctx){
		long nanosec = System.nanoTime();
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
	
		MonitorSequence ms = lc.getInstance();
		MonitorPoint mp = lc.isMonitorStarted() ? 
				ms.record(ctx) : ms.start(ctx);
		
		ms.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		
		return mp;
	}
	
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(){
		return pop(null, null, true);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(Marker marker){
		return pop(marker, null, false);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(Object message, boolean traceCallerStack){
		return pop(null, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(boolean traceCallerStack){
		return pop(null, null, traceCallerStack);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint pop(Marker marker, Object message, boolean traceCallerStack){
		CoreTrackingContext ctx = new CoreTrackingContext(DEFAULT_TRACKER_NAME);
		ctx.setCreateMillis(System.currentTimeMillis());
		ctx.setMessage(message);
		ctx.setMonitorMeta(newMonitorMeta(marker, traceCallerStack, 3));
		return pop(ctx);
	}
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint pop(TrackingContext ctx){
		long nanosec = System.nanoTime();
		
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
		
		MonitorSequence ms = lc.getMonitorSequence();
		MonitorPoint mp = ms.end(ctx);
		ms.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		
		if(ms.isFinished()){
			lc.finish();
		}
		return mp;
	}
}
