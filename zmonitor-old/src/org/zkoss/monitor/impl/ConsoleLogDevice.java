/**ConsoleLogDevice.java
 * 2011/10/11
 * 
 */
package org.zkoss.monitor.impl;

import org.zkoss.monitor.spi.LogDevice;
import org.zkoss.monitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ConsoleLogDevice implements LogDevice{
	public void info(Object... args) {
		System.out.println(Strings.append(args));
	}
	public void debug(Object... args) {
		System.out.println(Strings.append(args));
	}
	public void warn(Throwable e, Object... strings) {
		System.err.println(e==null?"":e.getMessage() + " " + Strings.append(strings));
		if(e!=null)e.printStackTrace();
	}
	public void warn(Object[] strings) {
		System.err.println(Strings.append(strings));
	}
}
