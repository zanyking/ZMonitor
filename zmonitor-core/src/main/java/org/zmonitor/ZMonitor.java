/**
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.IOException;

import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.MonitorMetaBase;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.TrackingContextBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.marker.Marker;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.util.StackTraceElementFinder;

import static org.zmonitor.marker.Markers.*;

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
	
	/* **********************************
	 * Profilers utility methods.
	 * **********************************/
	
	/**
	 * @return true if there's a monitor sequence instance and which is started.
	 */
	public static boolean isMonitoring(){
		return ZMonitorManager.getInstance().getMonitorLifecycle().isMonitorStarted();
	}
	
	/**
	 * 
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint push(Object mesg){
		return push(MK_PUSH_ZM, mesg, false);
	}	
	/**
	 * If you want to start a {@link MonitorPoint} in your Java code manually, use this method.<br>
	 * <p>
	 * This method will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack.<br>
	 * So this method will be cause some performance overhead to your program, if you are able to compose a {@link Marker} by yourself, please use {@link #push(Marker, Object)} instead.
	 * </p>
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @param traceCallerStack
	 * @return A {@link MonitorPoint} that will contain caller's java source information.
	 * @see #push(Marker, Object)
	 */
	public static MonitorPoint push(Object mesg, boolean traceCallerStack) {
		return push(MK_PUSH_ZM, mesg, traceCallerStack);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint push(Marker marker, Object message, boolean traceCallerStack){
		return push(newNativeTrackingCtx(marker, message, traceCallerStack));
	}
	private static TrackingContextBase newNativeTrackingCtx(
			Marker marker, Object message, boolean traceCallerStack){
		
		StackTraceElement[] elements = StackTraceElementFinder.truncate(3);// for: newNativeTrackingCtx
																			// push, pop, record
		
		
		final MonitorMeta mm = newMonitorMeta(marker, elements);
		TrackingContextBase ctx = new TrackingContextBase(TRACKER_NAME_ZM, elements){
			public MonitorMeta newMonitorMeta() {
				return mm;
			}
		};
		ctx.setMessage(message);
		return ctx;
	}
	private static MonitorMeta newMonitorMeta(Marker marker, StackTraceElement[] elements){
		return new MonitorMetaBase(marker, TRACKER_NAME_ZM, elements, Thread.currentThread().getName());
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint push(TrackingContext ctx){
		long nanosec = System.nanoTime();
		long slSpMillis = System.currentTimeMillis();
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
		MonitorSequence ms = lc.init();
		MonitorPoint mp = lc.getState().start(ctx);
		ms.accumulateSelfSpend(System.nanoTime()- nanosec, 
				System.currentTimeMillis() - slSpMillis);
		
		return mp;
	}
	
	/**
	 *
	 * If you want to give a monitor point in your Java code manually, use this method.<br>
	 * It will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack. 
	 * 
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @return a {@link MonitorPoint} that will contain caller's java source information. 
	 */
	public static MonitorPoint pinpoint(Object mesg, boolean traceCallerStack){
		return pinpoint(MK_RECORD_ZM, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint pinpoint(String mesg){
		return pinpoint(MK_RECORD_ZM, mesg, false);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint pinpoint(Marker marker, Object message, boolean traceCallerStack){
		return pinpoint(newNativeTrackingCtx(marker, message, traceCallerStack));
	}
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint pinpoint(TrackingContext ctx){
		long nanosec = System.nanoTime();
		long slSpMillis = System.currentTimeMillis();
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
	
		MonitorSequence ms = lc.init();
		
		MonitorPoint mp = lc.isMonitorStarted() ? 
				lc.getState().record(ctx) : 
				lc.getState().start(ctx);
		
		ms.accumulateSelfSpend(System.nanoTime()- nanosec, 
			System.currentTimeMillis() - slSpMillis);
				
		return mp;
	}
	
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(Object message, boolean traceCallerStack){
		return pop(MK_END_ZM, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(boolean traceCallerStack){
		return pop(MK_END_ZM, null, traceCallerStack);
	}
	/**
	 * 
	 * @param marker
	 * @param message
	 * @param traceCallerStack
	 * @return
	 */
	public static MonitorPoint pop(Marker marker, Object message, boolean traceCallerStack){
		return pop(newNativeTrackingCtx(marker, message, traceCallerStack));
	}
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public static MonitorPoint pop(TrackingContext ctx){
		long nanosec = System.nanoTime();
		long slSpMillis = System.currentTimeMillis();
		
		MonitorLifecycle lc = ctx.getLifeCycle();
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(ctx)) {
			return null;
		}
		
		MonitorSequence ms = lc.getMonitorSequence();
		MonitorPoint mp = lc.getState().end(ctx);
		ms.accumulateSelfSpend(System.nanoTime()- nanosec, 
				System.currentTimeMillis() - slSpMillis);
		
		if(lc.getState().isFinished()){
			lc.finish();
		}
		return mp;
	}
	/**
	 * pop out a monitor point stack without any new mp been tagged.
	 * @param lc
	 * @return
	 */
	public static MonitorPoint pop(MonitorLifecycle lc){
		long nanosec = System.nanoTime();
		long slSpMillis = System.currentTimeMillis();
		
		if(lc==null){
			throw new IllegalStateException("Not able to retrieve a MonitorLifecycle. " +
				"please take a look at the implementation of MonitorLifecycleManager, it must always returned a value instead of null.");
		}
		if(!lc.shouldMonitor(null)) {
			return null;
		}
		
		MonitorSequence ms = lc.getMonitorSequence();
		MonitorPoint mp = lc.getState().end(null);
		ms.accumulateSelfSpend(System.nanoTime()- nanosec, 
				System.currentTimeMillis() - slSpMillis);
		
		if(lc.getState().isFinished()){
			lc.finish();
		}
		return mp;
	}
}
