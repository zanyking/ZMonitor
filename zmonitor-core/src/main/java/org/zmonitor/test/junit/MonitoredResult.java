/**
 * 
 */
package org.zmonitor.test.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.SelectionEntryBase;
import org.zmonitor.selector.Selection;
import org.zmonitor.selector.Selectors;
import org.zmonitor.selector.impl.EntryIterator;
import org.zmonitor.selector.impl.zm.MPWrapper;
import org.zmonitor.selector.impl.zm.MSWrapper;
import org.zmonitor.util.Iterators;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitoredResult {
	
	LinkedList<MonitorSequence> mss = 
			new LinkedList<MonitorSequence>();

	void add(MonitorSequence ms) {
		mss.add(ms);
	}
	
	public MonitorSequence peek(){
		return mss.peek();
	}
	
	public MonitorSequence get(int index){
		return mss.get(index);
	}
	
	public MonitorSequence poll(){
		return mss.poll();
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
	
	
	/**
	 * 
	 * @return
	 */
	public Selection<MonitorPoint> asSelection(){
		List<MonitorSequence> res = getAll();
		if(res==null || res.isEmpty()){
			return new SelectionEntryBase<MonitorPoint>(
					Collections.EMPTY_LIST);
		}
		if(res.size()==1){
			return  new SelectionEntryBase<MonitorPoint>(
					new MSWrapper(res.get(0)));
			
		}

		List<MSWrapper> msws = new ArrayList<MSWrapper>(res.size());
		for(MonitorSequence ms : res){
			msws.add(new MSWrapper(ms));
		}
		return new SelectionEntryBase<MonitorPoint>(msws);
	}
	


}//end of class...


/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
class Selectors2{
	
	//--------------------------- MP Related ------------------
//	/**
//	 * 
//	 * @author Ian YT Tsai(Zanyking)
//	 *
//	 */
//	public interface Visitor<T>{
//		/**
//		 * 
//		 * @param mp the monitor point 
//		 * @return true, if the visits should continue, false to break it.
//		 */
//		public boolean visit(T mp);
//	}
//	/**
//	 * 
//	 * @param ms
//	 * @param selector
//	 * @param visitor
//	 */
//	public static void visit(MonitorSequence ms, String selector, Visitor<MonitorPoint> visitor){
//		Iterator<Entry> iterator = toIterator(ms, selector);
//		boolean shouldContinue = true;
//		while(iterator.hasNext()){
//			shouldContinue = visitor.visit((MonitorPoint) iterator.next().getObject());
//			if(!shouldContinue){
//				break;
//			}
//		}
//	}

		/**
		 * 
		 * @param ms
		 * @param selector
		 * @return
		 */
		public static Iterator<Entry<MonitorPoint>> toIterator(MonitorSequence ms, String selector){
			return new EntryIterator<MonitorPoint>(
					new MSWrapper(ms), selector);
		}
		/**
		 * 
		 * @param mp
		 * @param selector
		 * @return
		 */
		public static Iterator<Entry<MonitorPoint>> toIterator(MonitorPoint mp, String selector){
			return new EntryIterator<MonitorPoint>(
					new MPWrapper(mp, null), selector);
		}
		/**
		 * 
		 * @param mp
		 * @param selector
		 * @return
		 */
		public static List<MonitorPoint> find(MonitorPoint mp,  String selector){
			return Selectors.toValueList(toIterator(mp, selector));
		}
		/**
		 * 
		 * @param ms
		 * @param selector
		 * @return
		 */
		public static List<MonitorPoint> find(MonitorSequence ms, String selector){
			return Selectors.toValueList(toIterator(ms, selector));
		}
		
	
}
