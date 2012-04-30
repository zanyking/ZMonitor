/**MPContext.java
 * 2011/4/5
 * 
 */
package org.zkoss.monitor;

import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.spi.MeasurePointInfoFactory;
import org.zkoss.monitor.spi.Name;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPContext {
	private final StackTraceElement sElemt;
	private final String mpType;
	private Name name;
	private String mesg;
	private final long createMillis;
	
	private static MeasurePointInfoFactory getRenderer(){
		 return ZMonitorManager.getInstance().getMeasurePointInfoFactory() ;
	}
	
	/**
	 * 
	 * @param sElemt
	 * @param mpType
	 * @param name
	 * @param mesg
	 * @param createMillis 
	 */
	public MPContext(StackTraceElement sElemt, String mpType, Name name, String mesg, long createMillis) {
		super();
		this.sElemt = sElemt;
		this.mpType = mpType;
		this.name = name;
		if(name==null){
			this.name = getRenderer().getName(sElemt, mpType);
		}
		if(this.name==null){
			this.name = new StringName(mpType);
		}
		if(mesg==null){
			this.mesg = getRenderer().getMessage(sElemt, mpType);
		}else{
			this.mesg = mesg;	
		}
		
		this.createMillis = createMillis;
	}
	
	public boolean isStart(){
		return mpType.equals(ZMonitor.START);
	}
	public boolean isEnd(){
		return mpType.equals(ZMonitor.END);
	}
	public boolean isRecording(){
		return mpType.equals(ZMonitor.RECORDING);
	}
	
	public Name getName() {
		return name;
	}
	public void setName(Name name) {
		if(name==null)
			throw new IllegalArgumentException("name cannot be null!");
		this.name = name;
	}
	public String getMesg() {
		return mesg;
	}
	public void setMesg(String mesg) {
		this.mesg = mesg;
	}
	public StackTraceElement getsElemt() {
		return sElemt;
	}
	public String getMpType() {
		return mpType;
	}
	public long getCreateMillis() {
		return createMillis;
	}
	
	
}
