/**
 * 
 */
package org.zmonitor.webtest.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorSequence;
import org.zmonitor.web.WebMonitorMeta;
import org.zmonitor.webtest.MonitorSequenceStore;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SimpleMonitorSequenceStore implements MonitorSequenceStore{

	private final List< MonitorSequence> mss = 
		Collections.synchronizedList(new ArrayList<MonitorSequence>());

	private final Set< String> uuids = 
			Collections.synchronizedSet(new HashSet<String>());

	public List<MonitorSequence> drainAll() {
		ArrayList<MonitorSequence> list = new ArrayList<MonitorSequence>(mss);
		mss.clear();
		return list;
	}

//	public List<MonitorSequence> peek() {
//		return new ArrayList<MonitorSequence>(mss);
//	}
	public void add( MonitorSequence ms) {
		mss.add(ms);
	}

	public void addAll(Collection<MonitorSequence> mss) {
		for(MonitorSequence ms : mss){
			MonitorMeta m = ms.getRoot().getMonitorMeta();
			if(m instanceof WebMonitorMeta){
				WebMonitorMeta wm = (WebMonitorMeta) m;
				String uuid = wm.getUuid();
				if(uuid!=null){
					this.uuids.add(uuid);
				}
			}
		}
		this.mss.addAll(mss);
	}

	public boolean contains(String uuid) {
		return uuids.contains(uuid);
	}
	

}
