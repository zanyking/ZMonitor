/**
 * 2011/3/23
 * 
 */
package org.zmonitor.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
public class SampleConsoleMonitorSequenceHandler extends ZMBeanBase 
	implements MonitorSequenceHandler, CustomConfigurable {
	
	
	private static SimpleDateFormat getHHmmssSSS_yyyy_MM_dd(){
		return new SimpleDateFormat("HH:mm:ss:SSS yyyy/MM/dd");
	}
	public SampleConsoleMonitorSequenceHandler(){}
	
	public void handle(MonitorSequence ms){
		//dump Records from Black Box
		
		MonitorPoint root = ms.getRoot();
		
		String indent = "    ";
		String totalElipsd = align(retrieveMillisToEnd(root));
		StringBuffer sb = new StringBuffer();
		
		Strings.appendln(sb, "[ ",getHHmmssSSS_yyyy_MM_dd().format(new Date())," ] ",
				root.getMonitorMeta().getTrackerName()," -> MONITOR_SEQUENCE DUMP BEGIN");
		
		Strings.appendln(sb, "[",totalElipsd,"]ms Elipsed - MP amount:",ms.getRecordAmount(), 
				", self spend Nanosec: ", Strings.toNumericString(ms.getSelfSpendNanos(),","),
				". self spend millis: ", Strings.toNumericString(ms.getSelfSpendMillis(),","));
		Strings.appendln(sb, indent,"[ pre~ | ~next ]ms");
		
		writeRoot(sb, root, indent, indent);
		
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
	 * @param root
	 * @param prefix
	 * @param indent
	 */
	public void writeRoot(StringBuffer sb, MonitorPoint root, String prefix, String indent){
		String mesgPfx = Strings.append(prefix, "[",align(0),
				"|",align(retrieveMillisToEnd(root)),"]ms [",root.getMonitorMeta(),"]");
//				"|",Strings.alignedMillisStr(record.getSelfPeriod()),"]ms [",record.name,"]");
		
		Strings.appendln(sb, mesgPfx , " total MPs:",root.getMonitorSequence().getCounter(), " - ",root.getMessage());
		
		String childPrefix = prefix+indent;
		for(MonitorPoint child : root.getChildren()){
			write(sb, child, childPrefix, indent);
		}
	}
	private SelectorAdaptor selAdptor = ZMonitorManager.getInstance().getSelectorAdaptor();
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
	
	
	public void writeMP(StringBuffer sb, MonitorPoint mp, String prefix){
		String mpId = selAdptor.retrieveId(mp);
		Set<String> mpCssClz = selAdptor.retrieveConceptualCssClasses(mp);
		String mpType = selAdptor.retrieveType(mp);
		
		String mesgPfx = Strings.append(prefix, "[",
				align(retrieveMillisToPrevious(mp)),
				"|",align(retrieveMillisToNext(mp)),"]ms",
				", type=\"", mpType,"\"",
				", class=\"", mpCssClz,"\"",
				", id=\"", mpId,"\""
				);
//				"|",Strings.alignedMillisStr(record.getSelfPeriod()),"]ms [",record.name,"]");
		
		Strings.appendln(sb, mesgPfx ," - ",mp.getMessage());
	}
	
	
	
	
	private String toString(Marker marker, String seperator){
		StringBuffer sb = new StringBuffer();
		Iterator<Marker> itor = marker.iterator();
		Marker mk = null;
		while(itor.hasNext() ){
			 mk = itor.next();
			 sb.append(toString(mk, seperator)).append(seperator);
		}
		sb.append(marker.getName());
		return sb.toString();
	}

	public void configure(ConfigContext webConf) {
		// TODO Auto-generated method stub
	}


	public static String align(long ms){
		String prefix = "";
		     if(ms < 10) prefix = "    ";
		else if(ms < 100) prefix = "   ";
		else if(ms < 1000) prefix = "  ";
		else if(ms < 10000) prefix = " ";
//		else if(ms < 100000) prefix = " ";
		return prefix + ms;
	}


	
}
