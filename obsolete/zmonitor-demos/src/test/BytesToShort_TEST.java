package test;

import java.io.FileInputStream;
import java.io.ObjectStreamConstants;

import org.zkoss.monitor.server.grizzly.ObjSerStreamByteArray;

public class BytesToShort_TEST {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		short a = ObjectStreamConstants.STREAM_MAGIC;
		System.out.println("STREAM_MAGIC: "+Integer.toHexString(a));
		
//		String filePath = "C:\\ZM_LOG\\zmonitor_dosend_0.log";
		String filePath = "C:\\ZM_LOG\\zmonitor_dosend_1.log";
//		String filePath = "C:\\ZM_LOG\\zmonitor_dosend_0.log";
//		String filePath = "C:\\ZM_LOG\\zmonitor_receive_2.log";
		
		
		FileInputStream fin = new FileInputStream(filePath);
		byte[] two = new byte[2];
		fin.read(two);
		fin.close();
		short s = ObjSerStreamByteArray.extractHeader(two);
		
		System.out.println("read bytes:"+Integer.toHexString(s));
	}

}
