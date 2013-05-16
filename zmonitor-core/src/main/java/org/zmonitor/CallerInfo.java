/**
 * 
 */
package org.zmonitor;

import java.io.Serializable;

import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CallerInfo implements Serializable {
	private static final long serialVersionUID = -2036093624490603264L;
	protected String className; 
	protected String methodName; 
	protected int lineNumber = -1;
	
	public CallerInfo(){};
	
	public CallerInfo(String className, String methodName, int lineNumber) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public CallerInfo(StackTraceElement stEle) {
		className = stEle.getClassName();
		methodName = stEle.getMethodName();
		lineNumber = stEle.getLineNumber();
//		stEle.isNativeMethod();
//		stEle.getFileName();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result + lineNumber;
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
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
		CallerInfo other = (CallerInfo) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}
}
