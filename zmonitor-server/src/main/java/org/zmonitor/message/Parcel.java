/**
 * 
 */
package org.zmonitor.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class Parcel implements Serializable, Iterable<Message> {

	private static final long serialVersionUID = -3777430084074118068L;
	private final List<Message> messages; 
	private String agentId;
	
	public Parcel(){
		messages = new ArrayList<Message>(30);
	}
	
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String id) {
		this.agentId = id;
	}

	public void add(Message tl){
		messages.add(tl);
	}
	
	public void add(List<Message> tls){
		messages.addAll(tls);
	}

	public List<Message> getAll(){
		return messages;
	}
	
	public Iterator<Message> iterator(){
		return messages.iterator();
	}
	
}
