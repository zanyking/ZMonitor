/**
 * 
 */
package org.zmonitor.handler.text;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;

/**
 * A context object with cursor concept pointed to a {@link MonitorPoint}.
 * a renderer can accept this object as an argument to perform rendering task.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface RenderContext {
	/*
	 * Handle Context API
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
	RenderContext next();
}
