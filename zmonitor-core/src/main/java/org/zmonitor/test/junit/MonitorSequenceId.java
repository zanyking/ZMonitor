package org.zmonitor.test.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface MonitorSequenceId {
	/**
	 * 
	 * @return an id used to tag a monitor sequence.
	 */
	String value();
	
}
