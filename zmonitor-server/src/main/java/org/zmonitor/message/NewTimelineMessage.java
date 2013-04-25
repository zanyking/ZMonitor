/**HandleTimelineMessage.java
 * 2011/10/14
 * 
 */
package org.zmonitor.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class NewTimelineMessage extends Message implements Iterable<MonitorSequence>{
	private static final long serialVersionUID = 196165525987720297L;
	
	private final List<MonitorSequence> timelines; 
	
	public NewTimelineMessage(){
		timelines = new ArrayList<MonitorSequence>(30);
	}
	
	public void add(MonitorSequence tl){
		timelines.add(tl);
	}
	
	public void add(List<MonitorSequence> tls){
		timelines.addAll(tls);
	}

	public List<MonitorSequence> getAll(){
		return timelines;
	}
	
	public Iterator<MonitorSequence> iterator(){
		return timelines.iterator();
	}
}
