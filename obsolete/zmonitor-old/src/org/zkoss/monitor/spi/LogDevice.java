/**
 * 
 */
package org.zkoss.monitor.spi;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface LogDevice {
	public void info(Object... args);
	public void debug(Object... args);
	public void warn( Throwable e, Object... strings) ;
	public void warn(Object[] strings);
}
