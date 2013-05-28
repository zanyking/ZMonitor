/**
 * 
 */
package org.zmonitor.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorResult {
	
	LinkedList<MonitorSequence> repo = 
			new LinkedList<MonitorSequence>();

	public void add(MonitorSequence ms) {
		repo.add(ms);
	}
	
	public MonitorSequence peek(){
		return repo.peek();
	}
	
	public MonitorSequence get(int index){
		return repo.get(index);
	}
	
	public MonitorSequence poll(){
		return repo.poll();
	}
	
	public List<MonitorSequence> pollAll(){
		ArrayList<MonitorSequence> arr = 
				new ArrayList<MonitorSequence>(repo);
		repo.clear();
		return arr;
	}
	
	
	
	


}
