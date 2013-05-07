/**
 * 
 */
package org.zmonitor.handler.text;

import java.io.Writer;

/**
 * 
 * provides a writer and a bunch of utility methods for easy write. 
 * utility writing methods.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface WriteContext extends RenderContext{
	
	
	/*
	 * Write API
	 */
	
	/**
	 * The writer which handles the OutputStream
	 * @return
	 */
	Writer getWriter();
	
	/**
	 * 
	 * @return a proper indentation for current MonitorPoint
	 */
	String getIndent();
	
	/**
	 * 
	 */
	void writeln();
	
	
	
	
	
	
	
}
