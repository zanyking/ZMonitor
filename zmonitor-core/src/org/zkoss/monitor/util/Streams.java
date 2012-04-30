/* Streams.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2008/11/12, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.


*/
package org.zkoss.monitor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Streams {

	/**
	 * 
	 * @param in
	 * @param out
	 * @param chunkSize unit is byte.
	 * @throws IOException
	 */
	public static void flush(InputStream in, OutputStream out, int chunkSize) throws IOException{
		byte[] b = new byte[chunkSize];
		flush(in, out, b);
	}
	/**
	 * 
	 * @param in
	 * @param out
	 * @param chunkSize unit is byte.
	 * @throws IOException
	 */
	public static void flush(InputStream in, OutputStream out, byte[] chunk) throws IOException{
		int readLen = -1;
		while( (readLen = in.read(chunk)) != -1){
			out.write(chunk, 0, readLen);
		}
		out.flush();
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String readTextContent(File file, int chunkSize) throws FileNotFoundException{
		return readTextContent(new FileInputStream(file), chunkSize);
	}
	/**
	 * 
	 * @param in
	 * @param chunkSize
	 * @return
	 */
	public static String readTextContent(InputStream in, int chunkSize){
		return readTextContent(in, chunkSize, "UTF-8");
	}
	/**
	 * 
	 * @param in
	 * @param chunkSize
	 * @param encoding
	 * @return
	 */
	public static String readTextContent(InputStream in, int chunkSize, String encoding){
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			 flush(in, bout, chunkSize);
			 return new String(bout.toByteArray(), encoding);
		}catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if(in !=null)in.close();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
	}
	/**
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(obj);
		oout.close();
		return bout.toByteArray(); 
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String readTextContent(String path, int chunkSize) throws IOException{
		File file = new File(path);
		if(!file.exists())
			throw new IllegalArgumentException("there's no such file! "+path);
		return readTextContent(file, chunkSize);
	}

}
