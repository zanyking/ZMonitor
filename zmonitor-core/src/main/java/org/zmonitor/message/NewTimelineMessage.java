/**HandleTimelineMessage.java
 * 2011/10/14
 * 
 */
package org.zmonitor.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zmonitor.Timeline;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class NewTimelineMessage extends Message implements Iterable<Timeline>{
	private static final long serialVersionUID = 196165525987720297L;
	
	private final List<Timeline> timelines; 
	
	public NewTimelineMessage(){
		timelines = new ArrayList<Timeline>(30);
	}
	
	public void add(Timeline tl){
		timelines.add(tl);
	}
	
	public void add(List<Timeline> tls){
		timelines.addAll(tls);
	}

	public List<Timeline> getAll(){
		return timelines;
	}
	
	public Iterator<Timeline> iterator(){
		return timelines.iterator();
	}
}
