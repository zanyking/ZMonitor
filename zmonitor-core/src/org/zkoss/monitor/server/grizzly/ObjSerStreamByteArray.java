/**
 * 
 */
package org.zkoss.monitor.server.grizzly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamConstants;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public final class ObjSerStreamByteArray {

	private ByteArrayOutputStream bout;
	
	public ObjSerStreamByteArray(){}
	public ObjSerStreamByteArray(byte[] arr){
		append(arr);
	}
	/**
	 * 
	 * @param arr data chunk need to append to this byteArray.
	 * @throws IncorrectHeaderException if this ByteArray is empty
	 *  and the given arr is not start with: ObjectStreamConstants.STREAM_MAGIC
	 */
	public void append(byte[] arr){
		if(arr==null || arr.length==0){
			return;
		}
		if(bout==null){// init...
			if(!startWithHead(arr)){
				throw new IncorrectHeaderException(
					"must start with ObjectStreamConstants.STREAM_MAGIC: "+
					new String(new byte[]{(byte) ObjectStreamConstants.STREAM_MAGIC}));
			}
			bout = new ByteArrayOutputStream();
		}
		try {
			bout.write(arr);
		} catch (IOException e) {}
		
	}
	/**
	 * 
	 * @return
	 */
	public byte[] toArray(){
		return bout.toByteArray();
	}
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static boolean startWithHead(byte[] data){
		return extractHeader(data) == ObjectStreamConstants.STREAM_MAGIC;
	}
	/**
	 * extract the first 2 bytes and convert it to short.
	 * @param arr
	 * @return
	 */
	public static short extractHeader(byte[] arr){
		if(arr.length<2)
			throw new IllegalArgumentException("input array length must >= 2! arr.length: "+arr.length);
		short high = arr[0];
		short low = arr[1];
		return  (short) (((high & 0xFF) << 8) | (low & 0xFF));
	}
	/**
	 * extract the last 2 bytes and convert it to short.
	 * @param arr
	 * @return
	 */
	public static short extractEnd(byte[] arr){
		if(arr.length<2)
			throw new IllegalArgumentException("input array length must >= 2! arr.length: "+arr.length);
		short high = arr[arr.length - 2];
		short low = arr[arr.length - 1];
		return  (short) (((high & 0xFF) << 8) | (low & 0xFF));
	}
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class IncorrectHeaderException extends RuntimeException{
		IncorrectHeaderException(String str){
			super(str);
		}
	}//end of class...
	
	
}
