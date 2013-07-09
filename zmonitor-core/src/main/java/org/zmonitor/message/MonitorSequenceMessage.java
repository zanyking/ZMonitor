package org.zmonitor.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorSequenceMessage extends Message implements Iterable<MonitorSequence>{
	private static final long serialVersionUID = 196165525987720297L;
	
	private final List<MonitorSequence> mss = 
			new ArrayList<MonitorSequence>(30);
	
	public void add(MonitorSequence tl){
		mss.add(tl);
	}
	
	public void add(List<MonitorSequence> tls){
		mss.addAll(tls);
	}

	public List<MonitorSequence> getAll(){
		return mss;
	}
	
	public Iterator<MonitorSequence> iterator(){
		return mss.iterator();
	}
}
