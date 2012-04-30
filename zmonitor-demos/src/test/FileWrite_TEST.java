/**Serialization.java
 * 2011/3/23
 * 
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.zkoss.monitor.MeasurePoint;

/**
 * @author Ian YT Tsai
 * 
 */
public class FileWrite_TEST {

	private static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat(
			"yyyy.MM.dd");
	private static final SimpleDateFormat yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Date date = new Date();
		File file = new File("C:\\TEMP\\zMonitor" + yyyy_MM_dd.format(date)
				+ ".log");
		writeLog("This is a log, " + yyyy_MM_dd_HH_mm_ss.format(date) + "\n",
				file);
		readBack(file);
	}

	public static void writeLog(String log, File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStreamWriter writer = null;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(file, true),
					"utf-8");
			writer.write(log);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private static List<MeasurePoint> readBack(File file) throws IOException {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			LineNumberReader lineReader = new LineNumberReader(
					new InputStreamReader(fin));
			String str = lineReader.readLine();
			while (str != null) {
				System.out.println("result[" + lineReader.getLineNumber()
						+ "]: " + str);
				str = lineReader.readLine();
			}
		} finally {
			fin.close();
		}

		return null;
	}

}
