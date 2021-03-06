/**
 * 
 */
package org.zmonitor.test.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.MonitorPointSelection;
import org.zmonitor.selector.MonitorPointSelectionBase;
import org.zmonitor.selector.impl.zm.MSWrapper;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitoredResult {
	
	private final List<MonitorSequence> mss;
	
	public MonitoredResult(List<MonitorSequence> mss) {
		this.mss = mss;
	}
	public MonitoredResult() {
		this.mss = Collections.synchronizedList(
				new LinkedList<MonitorSequence>());
	}
	
	void add(MonitorSequence ms) {
		mss.add(ms);
	}

	public MonitorSequence get(int index){
		return mss.get(index);
	}
	
	public List<MonitorSequence> getAll(){
		return new ArrayList<MonitorSequence>(mss);
	}
	
	public List<MonitorSequence> pollAll(){
		ArrayList<MonitorSequence> arr = 
				new ArrayList<MonitorSequence>(mss);
		mss.clear();
		return arr;
	}
	
	public MonitorPointSelection asSelection(){
		List<MonitorSequence> res = getAll();
		if(res==null || res.isEmpty()){
			return new MonitorPointSelectionBase(
					Collections.EMPTY_LIST);
		}
		if(res.size()==1){
			return  new MonitorPointSelectionBase(
					new MSWrapper(res.get(0)));
			
		}

		List<MSWrapper> msws = new ArrayList<MSWrapper>(res.size());
		for(MonitorSequence ms : res){
			msws.add(new MSWrapper(ms));
		}
		return new MonitorPointSelectionBase(msws);
	}

	public int size() {
		return mss.size();
	}
	

}//end of class...

