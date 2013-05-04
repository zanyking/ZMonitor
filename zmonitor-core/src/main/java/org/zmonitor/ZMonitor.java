/**ZMonitor.java
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.IOException;

import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.ConfigSources;
import org.zmonitor.impl.StringName;
import org.zmonitor.impl.ThreadLocalMonitorSequenceLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.spi.MonitorSequenceLifecycle;
import org.zmonitor.spi.Name;


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
	public static final String START = "START";
	public static final String RECORDING = "RECORDING";
	public static final String END = "END";
	
	
	
	private ZMonitor(){}
	
	/*
	 * if there's no one(ZK Interceptor, Servlet Filter, Log4j Appender)
	 * do the initialization before some part of the code load this class,
	 * the zmonitor will be in Pure Java Mode.
	 */
	static{ 
		if(!ZMonitorManager.isInitialized()){
			try {

				ZMonitorManager aZMonitorManager = new ZMonitorManager();
				
				//TODO: get Configuration Source...
				ConfigSource confSrc = ConfigSources.loadForSimpleJavaProgram();
				if(confSrc==null){
					ZMLog.warn("cannot find Configuration:["+
							ConfigSource.ZMONITOR_XML+"] from classpath.");
					ZMLog.warn("There's no configuration file loaded, the ZMonitorManager will be configured manually by developer himself.");
					ZMLog.warn("If you want to give your custom settings, " +
							"please provide a \""+ConfigSource.ZMONITOR_XML+"\" file under classpath.");
				}else{
					aZMonitorManager.performConfiguration(confSrc);	
				}
				aZMonitorManager.setLifecycleManager(
					new ThreadLocalMonitorSequenceLifecycleManager());
				
				ZMonitorManager.init(aZMonitorManager);// might through AlreadyStartedExec...
				
				ZMLog.info("Init ZMonitor in pure Java mode: " + ZMonitor.class);
			} catch (IOException e) {
				throw new IgnitionFailureException(e);
			} catch (AlreadyStartedException e) {
				// Do nothing or simply log...
				ZMLog.info("already initialized by other place");
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		ZMonitorManager.dispose();
	}
	

	
	private static MonitorSequenceLifecycle getLifecycle(){
		return ZMonitorManager.getInstance().getMonitorSequenceLifecycle();
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
	 * <ul>
	 * 	 <li>If the current {@link MonitorSequence} has never started before, this call will start it.</li>
	 *   <li>If the current {@link MonitorSequence} already started, this method will push a new {@link MonitorPoint} stack level,
	 *    and any newly added {@link MonitorPoint} will use this as it's parent.</li>
	 *   <li>To end up the current level(pop out the current {@link MonitorPoint}), you need to invoke {@link #pop()}.</li>
	 *   <li>You need to guarantee that one of the corresponding {@link #pop()} methods will be called if you call Start, 
	 *   if you failed to do so, the measur result of this {@link MonitorSequence} will be corrupted and might throw exception.</li>
	 * </ul>
	 *   
	 * @param name the identifier of this {@link MonitorPoint}.
	 * @param mesg the message of this {@link MonitorPoint}.
	 * @param traceCallerStack
	 * @return
	 * @see #push(String)
	 */
	public static MonitorPoint push(Name name, String mesg, boolean traceCallerStack) {
		return start0(name, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * @param name
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint push(Name name, String mesg){
		return start0(name, mesg, false);
	}
	/**
	 * If you want to start a {@link MonitorPoint} in your Java code manually, use this method.<br>
	 * <p>
	 * This method will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack.<br>
	 * So this method will be cause some performance overhead to your program, if you are able to compose a {@link Name} by yourself, please use {@link #push(Name, String)} instead.
	 * </p>
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @param traceCallerStack
	 * @return A {@link MonitorPoint} that will contain caller's java source information.
	 * @see #push(Name, String)
	 */
	public static MonitorPoint push(String mesg, boolean traceCallerStack) {
		return start0(null, mesg, traceCallerStack);
	}
	/**
	 * 
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint push(String mesg){
		return start0(null, mesg, false);
	}
	
	private static MonitorPoint start0(Name name, String mesg, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		long createMillis = System.currentTimeMillis();
		MonitorSequenceLifecycle lc = getLifecycle();
		
		if(lc==null || !lc.shouldMeasure(name, mesg, createMillis)) {
			return null;
		}
		MPContext mpCtx = new MPContext(
				getOuterCallerInfo(traceCallerStack, 3), START, name, mesg, createMillis);
		
		name = (mpCtx.getName()==null) ? 
				new StringName(START): mpCtx.getName();
		MonitorSequence ms = lc.getInstance();
		MonitorPoint mp = ms.start(name, mpCtx.getMesg(), createMillis);
		
		ms.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		return mp;
	}
	private static StackTraceElement getOuterCallerInfo(boolean shouldDo, int callerLevel){
		if(!shouldDo)return null;
		StackTraceElement[] stackElemts = Thread.currentThread().getStackTrace();
		callerLevel += 1;
		if(stackElemts.length<=callerLevel){
			//this should never happened...
			throw new Error("How could method has no caller?");
		}
		StackTraceElement sElemt = stackElemts[callerLevel];
		return sElemt;
	}
	
	/**
	 *
	 * If you want to give a measure point in your Java code manually, use this method.<br>
	 * It will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack. 
	 * 
	 * @param mesg the message that you want to assign to {@link MonitorPoint}.
	 * @return a {@link MonitorPoint} that will contain caller's java source information. 
	 */
	public static MonitorPoint record(String mesg, boolean traceCallerStack){
		return record0(null, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint record(String mesg){
		return record0(null, mesg, false);
	}
	/**
	 * recording using a customized {@link Name} to the result measure point.<br>
	 * The Profiler will use the measure point's name as an identifier, 
	 * so you need to make sure {@link Name#hashCode()} and {@link Name#equals(Object)} are implemented properly. 
	 * 
	 * @param name the identifier that you want to use while recording. 
	 * @param mesg the message that you want to record.
	 * @return a measure Point with a custom name.
	 */
	public static MonitorPoint record(Name name, String mesg, boolean traceCallerStack){
		return record0(name, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * 
	 * @param name
	 * @param mesg
	 * @return
	 */
	public static MonitorPoint record(Name name, String mesg){
		return record0(name, mesg, false);
	}
	private static MonitorPoint record0(Name name, String mesg, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		long createMillis = System.currentTimeMillis();
		MonitorSequenceLifecycle lc = getLifecycle();
		
		if(lc==null || !lc.shouldMeasure(name, mesg, createMillis))return null;
		boolean started = lc.isMonitorStarted();
		
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 3), 
				started?RECORDING:START, name, mesg, createMillis);
		MonitorSequence ms = lc.getInstance();
		
		MonitorPoint mp = started ? 
				ms.record(mpCtx.getName(), mpCtx.getMesg(), createMillis) 
				: ms.start(mpCtx.getName(), mpCtx.getMesg(), createMillis);
		
		ms.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		return mp;
	}
	
	/**
	 * This method will end the current level of the {@link MonitorSequence}, if the level is 0( it's the root level of {@link MonitorSequence}), 
	 * the {@link MonitorSequenceLifecycle#finish()} will be called to end this {@link MonitorSequence}'s life-cycle.<br>
	 * The caller of this method need to guarantee the corresponding {@link #push(Name, String)} has been called before,
	 * otherwise the profiling result will be corrupted.<br>
	 * 
	 * @throws IllegalStateException if {@link MonitorSequenceLifecycle#isMonitorStarted()} is false, 
	 * which means you didn't initialize or started a {@link MonitorSequence} before. 
	 * @return null if timeline already reach the root. 
	 */
	public static MonitorPoint pop(Name name, String message, boolean traceCallerStack){
		return end0(name, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(Name name, String message ){
		return end0(name, message, false);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop( String message, boolean traceCallerStack){
		return end0(null, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(boolean traceCallerStack){
		return end0(null, null, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(Name name){
		return end0(name, null, false);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MonitorPoint pop(){
		return end0(null, null, true);
	}
	private static MonitorPoint end0(Name name, String message, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		MonitorSequenceLifecycle lc = getLifecycle();
		
		if(lc==null || !lc.isMonitorStarted()){
			return null;
//			throw new IllegalStateException("You need to start a {@link Timeline} before end any level of it!");
		}
		long createMillis = System.currentTimeMillis();	
		if(!lc.shouldMeasure(name, message, createMillis))return null;
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 3), 
				END, name, message, createMillis);
		MonitorSequence tl = lc.getMonitorSequence();
		MonitorPoint mp = tl.end(mpCtx.getName(), mpCtx.getMesg());
		
		tl.accumulateSelfSpendNanos(System.nanoTime()- nanosec);
		if(tl.isFinished()){
			lc.finish();
		}
		
		return mp;
	}
	
	//TODO: not done yet....
	
//	private static void finish(){
//		if(!getLifecycle().isStarted()){
//			return;//nothing need to do...
//		}
//		Timeline tl = getLifecycle().getTimeline();
//		long createMillis = tl.getVeryEndCreateTime();
//		MPContext mpCtx;
//		while(!tl.isFinished()){
//			mpCtx = new MPContext(null, END, null, null, createMillis);
//			tl.end(mpCtx.getName(), mpCtx.getMesg());
//		}
//	}
}
