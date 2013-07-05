/**ToStringTimelineHandler.java
 * 2011/3/23
 * 
 */
package org.zkoss.monitor.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;
import org.zkoss.monitor.MeasurePoint;
import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.spi.CustomConfiguration;
import org.zkoss.monitor.spi.TimelineHandler;
import org.zkoss.monitor.util.DOMRetriever;
import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ToStringTimelineHandler implements TimelineHandler, CustomConfiguration {
	private static final SimpleDateFormat HHmmssSSS_yyyy_MM_dd = new SimpleDateFormat("HH:mm:ss:SSS yyyy/MM/dd");
	public ToStringTimelineHandler(){}
	
	public void handle(Timeline tl){
		//dump Records from Black Box
		long nano = System.nanoTime();
		String indent = "    ";
		String totalElipsd = Strings.alignedMillisStr(tl.getRoot().getBranchElipsedByEndTag());
		StringBuffer sb = new StringBuffer();
		
		Strings.appendln(sb, "[ ",HHmmssSSS_yyyy_MM_dd.format(new Date())," ] ",tl.getName()," -> TIMELINE DUMP BEGIN");
		Strings.appendln(sb, "[",totalElipsd,"]ms Elipsed - ZMonitor Measure Points:",tl.getRecordAmount(),
				", self spend Nanosec: ", Strings.toNumericString(tl.getSelfSpendNanosecond(),","));
		Strings.appendln(sb, indent,"[ previous ~ current | current ~ next ]ms");
		write(sb, tl.getRoot(), indent, indent);
		Strings.append(sb, tl.getName()," <- TIMELINE DUMP END, toStringTLHandler spent nanosecond: ");
		Strings.appendln(sb, Strings.toNumericString(System.nanoTime() - nano, ","));
		Strings.appendln(sb, "\n");
		toString(sb.toString());
	}
	
	protected void toString(String result){
		System.out.println(result);
	}
	
	private void write(StringBuffer sb, MeasurePoint record, String prefix, String indent){
		if(record==null)return;
		String mesgPfx = Strings.append(prefix, "[",Strings.alignedMillisStr(record.tickPeriod),
				"|",Strings.alignedMillisStr(record.getAfterPeriod()),"]ms [",record.name,"]");
//				"|",Strings.alignedMillisStr(record.getSelfPeriod()),"]ms [",record.name,"]");
		
		if(record.isLeaf()){
			Strings.appendln(sb, mesgPfx," - ",record.message);
			return;
		}
		
		Strings.appendln(sb, mesgPfx , " children:",record.children.size(), " - ",record.message);
		
		String childPrefix = prefix+indent;
		for(MeasurePoint child : record.children){
			write(sb, child, childPrefix, indent);
		}
	}
	public void destroy() {
		//Do nothing...
	}

	
	public void apply(ZMonitorManager manager, DOMRetriever xmlDoc,
			Node configNode) {
		// TODO Auto-generated method stub
		
	}

}
