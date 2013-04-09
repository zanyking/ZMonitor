/**VersionTreeStore.java
 * 2011/3/31
 * 
 */
package org.zkoss.monitor.handler;

import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.spi.TimelineHandler;
import org.zkoss.monitor.vtree.VersionTree;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class VersionTreeTimelineHandler implements TimelineHandler {
	
	private VersionTree versionTree = new VersionTree();
	
	public VersionTree getVersiontree() {
		return versionTree;
	}
	public synchronized void handle(Timeline execTLine) {
		versionTree.apply(execTLine);
	}
	public void destroy() {
		
	}
}
