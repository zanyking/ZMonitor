/**Profilers.java
 * 2011/3/4
 * 
 */
package org.zkoss.monitor;

import java.io.IOException;

import org.zkoss.monitor.impl.DummyConfigurator;
import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.impl.ThreadLocalTimelineLifecycleManager;
import org.zkoss.monitor.impl.XmlConfiguratorLoader;
import org.zkoss.monitor.impl.ZMLog;
import org.zkoss.monitor.spi.Configurator;
import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.spi.TimelineLifecycle;


/**
 * <p>
 * This class is designed to manually record a {@link MeasurePoint} to the current execution's {@link Timeline}.<br>
 * Imagine there are some executions of your java program, for example: 
 * <ul>
 *    <li>A user shoot a request to your Web Application.</li>
 *    <li>A user clicked a button on your App's UI, which will start a complicated business logic that require dozens of classes to involve in.</li>
 * </ul>  
 * Now, you want to know the detail information of these executions such as: 
 * <ul>
 *   <li>How long will it take from measuring point A to measuring point B?</li>
 *   <li>Which step(MP i to MP i+1) takes the longest period in the execution?</li>
 *   <li>What's the current value of Var i, when it comes to this step?</li>
 * </ul> 
 * The context that we used to store step's information and hierarchy is an <b>Execution Timeline</b>.
 * </p>
 * 
 *@author Ian YT Tsai(Zanyking)
 */
public final class ZMonitor {
	public static final String START = "START";
	public static final String RECORDING = "RECORDING";
	public static final String END = "END";
	
	
	
	private final static ZMonitor sZMonitor;
	private ZMonitor(){}
	
	/*
	 * if there's no one(ZK Interceptor, Servlet Filter, Log4j Appender) do the initialization before 
	 * then the profiler will be in Pure Java Mode.
	 */
	static{ 
		boolean isIgnitBySelf = false;
		if(!Ignitor.isIgnited()){
			try {
				Configurator conf = XmlConfiguratorLoader.loadForPureJavaProgram();
				if(conf==null){
					ZMLog.warn("cannot find Configuration:["+
							XmlConfiguratorLoader.ZMONITOR_XML+"] from classpath.");
					ZMLog.warn("System will get default configuration from: "+DummyConfigurator.class);
					ZMLog.warn("If you want to give your custom settings, " +
							"please give your own \""+XmlConfiguratorLoader.ZMONITOR_XML+"\" in classpath.");
					conf = new DummyConfigurator();
				}
				isIgnitBySelf = Ignitor.ignite( new ThreadLocalTimelineLifecycleManager(), conf);
				ZMLog.info("Init ZMonitor in pure Java mode: " + ZMonitor.class);
			} catch (IOException e) {
				throw new IgnitionFailureException(e);
			}
		}
		sZMonitor = isIgnitBySelf ? new ZMonitor(): null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		Ignitor.destroy();
	}
	


	/**
	 * 
	 * @return will create or return a timeline instance.
	 */
	private static Timeline getInstance(){
		return ZMonitorManager.getInstance().getTimelineLifecycle().getInstance();
	}
	
	private static TimelineLifecycle getLifecycle(){
		return ZMonitorManager.getInstance().getTimelineLifecycle();
	}
	
	private static MPInterceptor getMPInterceptor(){
		return ZMonitorManager.getInstance().getMPInterceptor();
	}
	/* **********************************
	 * Profilers utility methods.
	 * **********************************/
	
	/**
	 * @return true if there's a timeline instance and started.
	 */
	public static boolean isTimelineStarted(){
		return getLifecycle().hasTimelineStarted();
	}
	
	/**
	 * <ul>
	 * 	 <li>If the current {@link Timeline} has never started before, this call will start it.</li>
	 *   <li>If the current {@link Timeline} already started, this method will push a new {@link MeasurePoint} stack level,
	 *    and any newly added {@link MeasurePoint} will use this as it's parent.</li>
	 *   <li>To end up the current level(pop out the current {@link MeasurePoint}), you need to invoke {@link #pop()}.</li>
	 *   <li>You need to guarantee that one of the corresponding {@link #pop()} methods will be called if you call Start, 
	 *   if you failed to do so, the measur result of this {@link Timeline} will be corrupted and might throw exception.</li>
	 * </ul>
	 *   
	 * @param name the identifier of this {@link MeasurePoint}.
	 * @param mesg the message of this {@link MeasurePoint}.
	 * @param traceCallerStack
	 * @return
	 * @see #push(String)
	 */
	public static MeasurePoint push(Name name, String mesg, boolean traceCallerStack) {
		long nanosec = System.nanoTime();
		
		long createMillis = System.currentTimeMillis();
		if(!getLifecycle().shouldMeasure(name, mesg, createMillis))return null;
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 2), START, name, mesg, createMillis);
		getMPInterceptor().doBeforeCompose(mpCtx);
		MeasurePoint mp = getInstance().start(mpCtx.getName(), mpCtx.getMesg(), createMillis);
		
		mp.timeline.accumulateSelfSpendNanosec(System.nanoTime()- nanosec);
		return mp;
	}
	/**
	 * traceCallerStack is default false.
	 * @param name
	 * @param mesg
	 * @return
	 */
	public static MeasurePoint push(Name name, String mesg){
		return start0(name, mesg, false);
	}
	/**
	 * If you want to start a {@link MeasurePoint} in your Java code manually, use this method.<br>
	 * <p>
	 * This method will collect the caller's {@link StackTraceElement} information by retrieving current thread's programming stack.<br>
	 * So this method will be cause some performance overhead to your program, if you are able to compose a {@link Name} by yourself, please use {@link #push(Name, String)} instead.
	 * </p>
	 * @param mesg the message that you want to assign to {@link MeasurePoint}.
	 * @param traceCallerStack
	 * @return A {@link MeasurePoint} that will contain caller's java source information.
	 * @see #push(Name, String)
	 */
	public static MeasurePoint push(String mesg, boolean traceCallerStack) {
		return start0(null, mesg, traceCallerStack);
	}
	/**
	 * 
	 * @param mesg
	 * @return
	 */
	public static MeasurePoint push(String mesg){
		return start0(null, mesg, false);
	}
	private static MeasurePoint start0(Name name, String mesg, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		long createMillis = System.currentTimeMillis();
		if(!getLifecycle().shouldMeasure(name, mesg, createMillis)) {
			return null;
		}
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 3), 
				START, name, mesg, createMillis);
		
		getMPInterceptor().doBeforeCompose(mpCtx);
		name = (mpCtx.getName()==null) ? 
				new StringName(START): mpCtx.getName();
				
		MeasurePoint mp = getInstance().start(name, mpCtx.getMesg(), createMillis);
		
		mp.timeline.accumulateSelfSpendNanosec(System.nanoTime()- nanosec);
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
	 * @param mesg the message that you want to assign to {@link MeasurePoint}.
	 * @return a {@link MeasurePoint} that will contain caller's java source information. 
	 */
	public static MeasurePoint record(String mesg, boolean traceCallerStack){
		return record0(null, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * @param mesg
	 * @return
	 */
	public static MeasurePoint record(String mesg){
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
	public static MeasurePoint record(Name name, String mesg, boolean traceCallerStack){
		return record0(name, mesg, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 * 
	 * @param name
	 * @param mesg
	 * @return
	 */
	public static MeasurePoint record(Name name, String mesg){
		return record0(name, mesg, false);
	}
	private static MeasurePoint record0(Name name, String mesg, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		long createMillis = System.currentTimeMillis();
		if(!getLifecycle().shouldMeasure(name, mesg, createMillis))return null;
		boolean started = getLifecycle().hasTimelineStarted();
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 3), 
				started?RECORDING:START, name, mesg, createMillis);
		getMPInterceptor().doBeforeCompose(mpCtx);
		MeasurePoint mp = started ? getInstance().record(mpCtx.getName(), mpCtx.getMesg(), createMillis) 
				: getInstance().start(mpCtx.getName(), mpCtx.getMesg(), createMillis);
		
		mp.timeline.accumulateSelfSpendNanosec(System.nanoTime()- nanosec);
		return mp;
	}
	
	/**
	 * This method will end the current level of the {@link Timeline}, if the level is 0( it's the root level of {@link Timeline}), 
	 * the {@link TimelineLifecycle#reset()} will be called to end this {@link Timeline}'s life-cycle.<br>
	 * The caller of this method need to guarantee the corresponding {@link #push(Name, String)} has been called before,
	 * otherwise the profiling result will be corrupted.<br>
	 * 
	 * @throws IllegalStateException if {@link TimelineLifecycle#hasTimelineStarted()} is false, 
	 * which means you didn't initialize or started a {@link Timeline} before. 
	 * @return null if timeline already reach the root. 
	 */
	public static MeasurePoint pop(Name name, String message, boolean traceCallerStack){
		return end0(name, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MeasurePoint pop(Name name, String message ){
		return end0(name, message, false);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MeasurePoint pop( String message, boolean traceCallerStack){
		return end0(null, message, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MeasurePoint pop(boolean traceCallerStack){
		return end0(null, null, traceCallerStack);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MeasurePoint pop(Name name){
		return end0(name, null, false);
	}
	/**
	 * traceCallerStack is default false.
	 */
	public static MeasurePoint pop(){
		return end0(null, null, true);
	}
	private static MeasurePoint end0(Name name, String message, boolean traceCallerStack){
		long nanosec = System.nanoTime();
		if(!getLifecycle().hasTimelineStarted()){
			return null;
//			throw new IllegalStateException("You need to start a {@link Timeline} before end any level of it!");
		}
		long createMillis = System.currentTimeMillis();	
		if(!getLifecycle().shouldMeasure(name, message, createMillis))return null;
		MPContext mpCtx = new MPContext(getOuterCallerInfo(traceCallerStack, 3), 
				END, name, message, createMillis);
		Timeline tl = getLifecycle().getTimeline();
		MeasurePoint mp = tl.end(mpCtx.getName(), mpCtx.getMesg());
		
		tl.accumulateSelfSpendNanosec(System.nanoTime()- nanosec);
		if(tl.isFinished()){
			getLifecycle().reset();
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
