/**SimpleRecordRenderer.java
 * 2011/3/24
 * 
 */
package org.zkoss.monitor.impl;

import org.zkoss.monitor.spi.MeasurePointInfoFactory;
import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultMeasurePointInfoFactory implements MeasurePointInfoFactory{
	
	public Name getName(StackTraceElement sElemt, String mpType) {
		Name name;
		if(sElemt!=null){
			name = new JavaName("JAVA", 
					sElemt.getClassName(), 
					sElemt.getMethodName(), 
					sElemt.getLineNumber());	
		}else{
			name = new StringName(mpType);
		}
		return name;
	}

	public String getMessage(StackTraceElement sElemt, String mpType) {
		if(sElemt!=null){
			String className = sElemt.getClassName();
			int idx = className.lastIndexOf(".");
			if(idx>0){
				className = className.substring(idx);	
			}
			return Strings.append(className,"::",sElemt.getMethodName(),"(): "+sElemt.getLineNumber(),"  ");
		}
		return "";
	}
	

}
