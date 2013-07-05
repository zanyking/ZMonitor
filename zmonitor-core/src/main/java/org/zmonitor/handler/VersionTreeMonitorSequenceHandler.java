/**VersionTreeStore.java
 * 2011/3/31
 * 
 */
package org.zmonitor.handler;

import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.vtree.VersionTree;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class VersionTreeMonitorSequenceHandler extends ZMBeanBase implements MonitorSequenceHandler {
	
	private VersionTree versionTree = new VersionTree();
	
	public VersionTree getVersiontree() {
		return versionTree;
	}
	public synchronized void handle(MonitorSequence execTLine) {
		versionTree.apply(execTLine);
	}
}
