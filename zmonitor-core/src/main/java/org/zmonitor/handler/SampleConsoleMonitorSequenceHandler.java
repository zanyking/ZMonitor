/**
 * 2011/3/23
 * 
 */
package org.zmonitor.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SampleConsoleMonitorSequenceHandler extends ZMBeanBase implements MonitorSequenceHandler, CustomConfigurable {
	
	private static SimpleDateFormat getHHmmssSSS_yyyy_MM_dd(){
		return new SimpleDateFormat("HH:mm:ss:SSS yyyy/MM/dd");
	}
	public SampleConsoleMonitorSequenceHandler(){}
	
	public void handle(MonitorSequence tl){
		//dump Records from Black Box
		long nano = System.nanoTime();
		String indent = "    ";
		String totalElipsd = Strings.alignedMillisStr(tl.getRoot().getBranchElipsedByEndTag());
		StringBuffer sb = new StringBuffer();
		
		Strings.appendln(sb, "[ ",getHHmmssSSS_yyyy_MM_dd().format(new Date())," ] ",tl.getName()," -> TIMELINE DUMP BEGIN");
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
	
	private void write(StringBuffer sb, MonitorPoint mp, String prefix, String indent){
		if(mp==null)return;
		String mesgPfx = Strings.append(prefix, "[",Strings.alignedMillisStr(mp.tickPeriod),
				"|",Strings.alignedMillisStr(mp.getAfterPeriod()),"]ms [",mp.name,"]");
//				"|",Strings.alignedMillisStr(record.getSelfPeriod()),"]ms [",record.name,"]");
		
		if(mp.isLeaf()){
			Strings.appendln(sb, mesgPfx," - ",mp.message);
			return;
		}
		
		Strings.appendln(sb, mesgPfx , " children:",mp.size(), " - ",mp.message);
		
		String childPrefix = prefix+indent;
		for(MonitorPoint child : mp.getChildren()){
			write(sb, child, childPrefix, indent);
		}
	}

	public void configure(ConfigContext webConf) {
		// TODO Auto-generated method stub
	}



}
