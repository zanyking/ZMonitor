/**
 * 2011/3/23
 * 
 */
package org.zmonitor.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.marker.Marker;
import org.zmonitor.selector.SelectorAdaptor;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.Strings;
import static org.zmonitor.util.MPUtils.*;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class EclipseConsoleMonitorSequenceHandler extends ZMBeanBase 
	implements MonitorSequenceHandler, CustomConfigurable {
	//SampleConsoleMonitorSequenceHandler
	
	private static SimpleDateFormat getHHmmssSSS_yyyy_MM_dd(){
		return new SimpleDateFormat("HH:mm:ss:SSS yyyy/MM/dd");
	}
	public EclipseConsoleMonitorSequenceHandler(){}

	public void configure(ConfigContext webConf) {
		selAdptor = webConf.getManager().getSelectorAdaptor();
		System.out.println("ZMonitorManager.getInstance().getSelectorAdaptor()"
				+selAdptor);
	}
	
	private boolean showThreadName = true;
	
	public boolean isShowThreadName() {
		return showThreadName;
	}
	public void setShowThreadName(boolean showThreadName) {
		this.showThreadName = showThreadName;
	}
	
	public void handle(MonitorSequence ms){
		//dump Records from Black Box
		
		MonitorPoint root = ms.getRoot();
		
		String indent = "    ";
		StringBuffer sb = new StringBuffer();
		
		Strings.append(sb, root.getMonitorMeta().getTrackerName()," -> MONITOR_SEQUENCE DUMP BEGIN [ ",getHHmmssSSS_yyyy_MM_dd().format(new Date())," ] ");
		
		if(showThreadName)
			Strings.appendln(sb, "[ ", root.getMonitorMeta().getThreadName()," ]");
		else
			Strings.appendln(sb);
		
		Strings.appendln(sb,"Total Elipsed Millis:\t",retrieveMillisToEnd(root));
		Strings.appendln(sb,"Monitor Point Amount:\t",ms.getSize());
		Strings.appendln(sb,"Self Spent Nanosec:\t",Strings.toNumericString(ms.getSelfSpendNanos(),","));
		Strings.appendln(sb,"Self Spent millis:\t", Strings.toNumericString(ms.getSelfSpendMillis(),","));
		
		
		
		if(previousMillis || nextMillis)
			Strings.append(sb, "[");
		
		if(previousMillis)
			Strings.append(sb, " pre~ ");
		if(previousMillis && nextMillis)
			Strings.append(sb, "|");
		if(nextMillis)
			Strings.append(sb, " ~next ");
		
		if(previousMillis || nextMillis)
			Strings.append(sb, "]ms ");
		
		
		Strings.appendln(sb);
		
		write(sb, root, indent, indent);
		
		Strings.append(sb, root.getMonitorMeta().getTrackerName()," <- MONITOR_SEQUENCE DUMP END, toStringTLHandler spent nanosecond: ");
		
		Strings.appendln(sb, "\n");
		toString(sb.toString());
	}
	
	protected void toString(String result){
		System.out.println(result);
	}

	/**
	 * 
	 * @param sb
	 * @param mp
	 * @param prefix
	 * @param indent
	 */
	public void write(StringBuffer sb, MonitorPoint mp, String prefix, String indent){
		if(mp==null)return;
		
		writeMP(sb, mp, prefix);
		
		if(mp.isLeaf()){
			return;
		}
		
		String childPrefix = prefix+indent;
		for(MonitorPoint child : mp.getChildren()){
			write(sb, child, childPrefix, indent);
		}
	}
	
	private SelectorAdaptor selAdptor;
	
	private boolean previousMillis = true;
	private boolean nextMillis = true;
	private boolean callerJavaFile = true;
	private boolean showId = false;
	private boolean showType = true;
	private boolean showClass = true;
	private boolean showMessage = true;
	private boolean shortClassName = true;
	
	public void writeMP(StringBuffer sb, MonitorPoint mp, String prefix){
		String mpId = selAdptor.retrieveId(mp);
		Set<String> mpCssClz = selAdptor.retrieveConceptualCssClasses(mp);
		String mpType = selAdptor.retrieveType(mp);
		
		Strings.append(sb, prefix);
		if(previousMillis || nextMillis)
			Strings.append(sb, "[");
		
		if(previousMillis)
			Strings.append(sb, align(retrieveMillisToPrevious(mp)));
		if(previousMillis && nextMillis)
			Strings.append(sb, "|");
		if(nextMillis)
			Strings.append(sb, align(retrieveMillisToStrictNext(mp)));
		
		if(previousMillis || nextMillis)
			Strings.append(sb, "]ms ");
		
		if(callerJavaFile)
			writeTraceElement(sb, "", mp.getMonitorMeta());
		if(showId)
			Strings.append(sb, ", ID:", mpId);
		if(showType)
			Strings.append(sb, ", TYPE:", mpType);
		if(showClass)
			Strings.append(sb, ", CLASS:", mpCssClz);
		if(showMessage)
			Strings.append(sb, ", MESSAGE:",mp.getMessage());
		
		Strings.append(sb,"\n");
		
//				"|",Strings.alignedMillisStr(record.getSelfPeriod()),"]ms [",record.name,"]");
		
		
	}
	public void writeRoot2MP(StringBuffer sb, MonitorPoint mp, String indent){
		List<MonitorPoint> bloodline = getBloodline(mp);
		String prefix = indent;
		for(MonitorPoint current : bloodline){
			writeMP(sb, current, prefix+=indent);	
		}
	}
	private void writeTraceElement(StringBuffer sb, String prefix, MonitorMeta meta){
		if(meta.isCallerNotAvailable()){
			Strings.append(sb, prefix, "caller's stackTraceElement is not available.");
			return;
		}else{
			Strings.append(sb, prefix,"at ", 
					getMetaClassName(meta.getClassName()),".",
					meta.getMethodName(),"(",
					meta.getFileName(),":",meta.getLineNumber(),")");	
		}
	}
	
	private String getMetaClassName(String clzName){
		if(shortClassName){
			int i = clzName.lastIndexOf(".");
			if(i<0) return clzName;
			return clzName.substring(i+1);
		}else{
			return clzName;
		}
	}
	

	
	public static List<MonitorPoint> getBloodline(MonitorPoint mp){
		LinkedList<MonitorPoint> bloodline = new LinkedList<MonitorPoint>();
		bloodline.addFirst(mp);
		mp = mp.getParent();
		while(mp!=null){
			bloodline.addFirst(mp);
			mp = mp.getParent();
		}
		return bloodline;
	}

	private static String align(long ms){
		String prefix = "";
		     if(ms < 10) prefix = "    ";
		else if(ms < 100) prefix = "   ";
		else if(ms < 1000) prefix = "  ";
		else if(ms < 10000) prefix = " ";
		return prefix + ms;
	}
	
	public boolean isPreviousMillis() {
		return previousMillis;
	}
	public void setPreviousMillis(boolean previousMillis) {
		this.previousMillis = previousMillis;
	}
	public boolean isNextMillis() {
		return nextMillis;
	}
	public void setNextMillis(boolean nextMillis) {
		this.nextMillis = nextMillis;
	}
	public boolean isCallerJavaFile() {
		return callerJavaFile;
	}
	public void setCallerJavaFile(boolean callerJavaFile) {
		this.callerJavaFile = callerJavaFile;
	}
	public boolean isShowId() {
		return showId;
	}
	public void setShowId(boolean showId) {
		this.showId = showId;
	}
	public boolean isShowType() {
		return showType;
	}
	public void setShowType(boolean showType) {
		this.showType = showType;
	}
	public boolean isShowClass() {
		return showClass;
	}
	public void setShowClass(boolean showClass) {
		this.showClass = showClass;
	}
	public boolean isShowMessage() {
		return showMessage;
	}
	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}
	public boolean isShortClassName() {
		return shortClassName;
	}
	public void setShortClassName(boolean shortClassName) {
		this.shortClassName = shortClassName;
	}
}
