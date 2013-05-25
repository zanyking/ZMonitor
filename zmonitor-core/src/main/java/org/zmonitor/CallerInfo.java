/**
 * 
 */
package org.zmonitor;

import java.io.Serializable;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface CallerInfo extends Serializable {
	public static final int CODE_NOT_AVAILABLE = -1;
	public static final int CODE_NATIVE_METHOD = -2;// please take look at the javadoc of StackTraceElement
	
	
	/**
	 * @return 
	 */
	public boolean isNativeMethod();
	/**
	 * 
	 * @return
	 */
	String getClassName() ;
	/**
	 * 
	 * @return
	 */
	String getMethodName() ;

	/**
	 * 
	 * @return
	 */
	int getLineNumber();
	/**
	 * 
	 * @return
	 */
	String getFileName();
	/**
	 * 
	 * @return
	 */
	String getTrackerName();
	/**
	 * 
	 * @return
	 */
	Marker getMarker();
}
