/**
 * 
 */
package org.zmonitor.impl;

import java.util.regex.Matcher;

import org.zmonitor.MonitorMeta;
import org.zmonitor.Marker;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleMonitorMeta implements MonitorMeta{
	private static final long serialVersionUID = -2036093624490603264L;

	protected String className; 
	protected String methodName; 
	protected int lineNumber = CODE_NOT_AVAILABLE;
	protected String fileName;
	protected Marker marker;
	protected String trackerName;
	
	public SimpleMonitorMeta(){};
	/**
	 * @param marker
	 */
	public SimpleMonitorMeta(Marker marker){
		this.marker = marker;
	};
	/**
	 * @param stEle
	 */
	public SimpleMonitorMeta(Marker marker, StackTraceElement stEle) {
		this(marker);
		Matcher sl;
		className = stEle.getClassName();
		methodName = stEle.getMethodName();
		lineNumber = stEle.getLineNumber();
		fileName = stEle.getFileName();
	}
	/**
	 * 
	 * @param className
	 * @param methodName
	 * @param lineNumber
	 */
	public SimpleMonitorMeta(Marker marker, String className, String methodName, int lineNumber, String fileName) {
		this(marker);
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
		this.fileName = fileName;
	}

	
	/**
	 * @return 
	 */
	public boolean isNativeMethod(){
		return lineNumber==CODE_NATIVE_METHOD;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(className!=null)
			Strings.append(sb, className);
		if(methodName!=null)
			Strings.append(sb, " ", methodName);
		if(lineNumber>0)
			Strings.append(sb, " ", lineNumber);
		return sb.toString();
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Marker getMarker() {
		return marker;
	}
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	public String getTrackerName() {
		return trackerName;
	}
	public void setTrackerName(String trackerName) {
		this.trackerName = trackerName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + lineNumber;
		result = prime * result + ((marker == null) ? 0 : marker.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((trackerName == null) ? 0 : trackerName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMonitorMeta other = (SimpleMonitorMeta) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (marker == null) {
			if (other.marker != null)
				return false;
		} else if (!marker.equals(other.marker))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (trackerName == null) {
			if (other.trackerName != null)
				return false;
		} else if (!trackerName.equals(other.trackerName))
			return false;
		return true;
	}
	
	protected SimpleMonitorMeta clone(){
		SimpleMonitorMeta clone = new SimpleMonitorMeta();
		clone.className = this.className;
		clone.fileName = this.fileName;
		clone.lineNumber = this.lineNumber;
		clone.marker = this.marker;
		clone.methodName = this.methodName;
		clone.trackerName = this.trackerName;
		return clone;
	}
	public boolean isSimilar(MonitorMeta mMeta) {
		return equals(mMeta);
	}
	

}