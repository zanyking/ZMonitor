/**ConsoleLogDevice.java
 * 2011/10/11
 * 
 */
package org.zmonitor.impl;

import org.zmonitor.spi.LogDevice;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConsoleLogDevice implements LogDevice{
	private boolean debug;
	
	public void info(Object... args) {
		if(LogLevel.INFO.level >= logLevel.level)
			System.out.println(Strings.append(args));
	}
	public void debug(Object... args) {
		if(LogLevel.DEBUG.level >= logLevel.level)
			System.out.println(Strings.append(args));
	}
	public void warn(Throwable e, Object... strings) {
		if(LogLevel.WARN.level >= logLevel.level){
			System.err.println(e==null?"":e.getMessage() + " " + Strings.append(strings));
			if(e!=null)e.printStackTrace();	
		}
	}
	public void warn(Object[] strings) {
		if(LogLevel.WARN.level >= logLevel.level)
			System.err.println(Strings.append(strings));
	}
	public boolean isDebug() {
		return debug;
	}
	
	private LogLevel logLevel = LogLevel.WARN;
	
	public String getLogLevel(){ 
		return logLevel.name();
	}
	public void setLogLevel(String level){
		try{
			logLevel = LogLevel.valueOf(level.toUpperCase());
			
		}catch(Exception e){/* DO NOTHING*/}
	}
}

enum LogLevel{
	WARN(2),
	INFO(1),
	DEBUG(0);
	
	int level;

	private LogLevel(int level) {
		this.level = level;
	}
	
	
}
