/**
 * 
 */
package org.zmonitor.spi;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface LogDevice {
	public boolean isDebug();
	public void info(Object... args);
	public void debug(Object... args);
	public void warn( Throwable e, Object... strings) ;
	public void warn(Object[] strings);
}
