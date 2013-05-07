/**
 * 
 */
package org.zmonitor.handler.text;

import java.io.Writer;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;

/**
 * 
 * A context object with cursor concept of MonitorPoint and 
 * utility writing methods.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface TextRenderContext{
	
	/*
	 * Rendering Context API
	 */
	/**
	 * 
	 * @return
	 */
	MonitorSequence getMonitorSequence();
	
	/*
	 * MonitorPoint DFS Traverse API
	 */
	/**
	 * 
	 * @return
	 */
	MonitorPoint getCurrent();
	/**
	 * 
	 * @return
	 */
	boolean hasNext();
	/**
	 * 
	 * @return
	 */
	MonitorPoint next();
	
	/*
	 * Rendering Utility API
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
	
	
	void writeln();
	
	
	
	
	
	
	
}
