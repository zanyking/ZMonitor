/**JavaName.java
 * 2011/3/28
 * 
 */
package org.zkoss.monitor.impl;

import java.io.Serializable;

import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.util.Strings;



/**
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class JavaName implements Name, Serializable{

	private static final long serialVersionUID = 9123002940034548389L;
	public static final String DEFAULT_NAME = "JAVA";
	protected String type = DEFAULT_NAME;
	protected String className; 
	protected String methodName; 
	protected int lineNumber = -1;

	public JavaName(){}
	/**
	 * 
	 * @param type
	 */
	public JavaName(String type){
		this.type = type;
	}
	/**
	 * 
	 * @param type
	 * @param className
	 * @param methodName
	 * @param lineNumber
	 */
	public JavaName(String type, String className, String methodName, int lineNumber) {
		super();
		this.type = type;
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Strings.append(sb, type);
		if(className!=null)
			Strings.append(sb, " ", className);
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

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		JavaName other = (JavaName) obj;
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		if(className!=null)
			Strings.append(sb, " ", className);
		if(methodName!=null)
			Strings.append(sb, " ", methodName);
		if(lineNumber>0)
			Strings.append(sb, " ", lineNumber);
		return sb.toString();
	}

}
