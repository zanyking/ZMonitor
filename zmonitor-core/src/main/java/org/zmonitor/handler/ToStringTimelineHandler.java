/**ToStringTimelineHandler.java
 * 2011/3/23
 * 
 */
package org.zmonitor.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.spi.CustomConfiguration;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.spi.XMLConfiguration;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ToStringTimelineHandler implements MonitorSequenceHandler, CustomConfiguration {
	private static final SimpleDateFormat HHmmssSSS_yyyy_MM_dd = new SimpleDateFormat("HH:mm:ss:SSS yyyy/MM/dd");
	public ToStringTimelineHandler(){}
	
	public void handle(MonitorSequence tl){
		//dump Records from Black Box
		long nano = System.nanoTime();
		String indent = "    ";
		String totalElipsd = Strings.alignedMillisStr(tl.getRoot().getBranchElipsedByEndTag());
		StringBuffer sb = new StringBuffer();
		
		Strings.appendln(sb, "[ ",HHmmssSSS_yyyy_MM_dd.format(new Date())," ] ",tl.getName()," -> TIMELINE DUMP BEGIN");
		Strings.appendln(sb, "[",totalElipsd,"]ms Elipsed - ZMonitor Measure Points:",tl.getRecordAmount(),
				", self spend Nanosec: ", Strings.toNumericString(tl.getSelfSpendNanos(),","));
		Strings.appendln(sb, indent,"[ pre~ | ~next ]ms");
		write(sb, tl.getRoot(), indent, indent);
		Strings.append(sb, tl.getName()," <- TIMELINE DUMP END, toStringTLHandler spent nanosecond: ");
		Strings.appendln(sb, Strings.toNumericString(System.nanoTime() - nano, ","));
		Strings.appendln(sb, "\n");
		toString(sb.toString());
	}
	
	protected void toString(String result){
		System.out.println(result);
	}
	
	private void write(StringBuffer sb, MonitorPoint record, String prefix, String indent){
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
		for(MonitorPoint child : record.children){
			write(sb, child, childPrefix, indent);
		}
	}
	public void destroy() {
		//Do nothing...
	}

	

	public void apply(ZMonitorManager manager, XMLConfiguration config,
			Node configNode) {
		// TODO Auto-generated method stub
		
	}

}
