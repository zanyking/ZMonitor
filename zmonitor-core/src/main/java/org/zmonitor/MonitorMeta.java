/**
 * 
 */
package org.zmonitor;

import java.io.Serializable;

import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorMeta extends Serializable, Cloneable {
	public static final int CODE_NOT_AVAILABLE = -1;
	public static final int CODE_NATIVE_METHOD = -2;// please take look at the javadoc of StackTraceElement
	/**
	 * Verifying if the two MonitorMetas are created at the same tracking conditions.
	 *  <ol>
	 *  <li>The implementation must be transitive, if A is similar to B, then B must also be similar to A.
	 *  <li>the implementation must be transitive, if A is similar to B and B is similar to C implies A is similar to C.
	 *  </ol>
	 * @param mMeta
	 * @return
	 */
	public boolean isSimilar(MonitorMeta mMeta);
	/**
	 * @return 
	 */
	public boolean isNativeMethod();
	/**
	 * 
	 * @return
	 */
	public boolean isCallerNotAvailable();
	/**
	 * @return
	 */
	String getClassName() ;
	/**
	 * @return
	 */
	String getMethodName() ;
	/**
	 * @return
	 */
	int getLineNumber();
	/**
	 * @return
	 */
	String getFileName();
	/**
	 * @return
	 */
	String getTrackerName();
	/**
	 * 
	 * @return
	 */
	StackTraceElement[] getStackTraceElements();
	/**
	 * @return
	 */
	Marker getMarker();
	/**
	 * 
	 * @param marker
	 */
	void setMarker(Marker marker);
}
