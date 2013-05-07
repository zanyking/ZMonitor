/**
 * 2011/3/4
 * 
 */
package org.zmonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zmonitor.spi.Name;




/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class MonitorPoint implements Serializable{
	private static final long serialVersionUID = 1772552143735347953L;
	
	public MonitorSequence mSequence;
	public long createMillis;
	public long tickPeriod;
	public int stack;
	public int index;
	public MonitorPoint parent;
	private ArrayList<MonitorPoint> children;	
	
	public Name name;
	public String message;
	public long finalEnd;// Measure Point
	
	/**
	 * 
	 * @param parent
	 * @param mesg
	 */
	public MonitorPoint(Name name, String mesg, MonitorPoint parent, boolean isLeaf, MonitorSequence bb, long createMillis) {
		
		mSequence = bb;
		mSequence.increament();
		this.createMillis = createMillis;
		this.name = name;
		this.message = mesg;//TODO: has potential to be an object...
		this.parent = parent;
		this.stack = (parent==null) ? 0 : parent.stack+1;
		
		this.children = isLeaf ? null : new ArrayList<MonitorPoint>(10);
		MonitorPoint previousSibling = (parent==null) ?  
				null : parent.getLastChild();
		if(previousSibling==null){
			if(parent!=null){
				tickPeriod = createMillis - parent.createMillis;
			}else{
				tickPeriod = 0;	
			}
		}else{
			tickPeriod = createMillis - previousSibling.createMillis;
		}
		if(parent!=null){
			index = parent.children.size(); 
			parent.children.add(this);
		}else{
			index = 0;
		}
	}

	public List<MonitorPoint> getChildren(){
		return children;
	}
	
	/**
	 * 
	 * @return the amount of kids.
	 */
	public int size(){
		return children.size();
	}
	/**
	 * search the 
	 * @param finalMillis
	 */
	public void markFinalEnd(long finalMillis) {
		finalEnd = finalMillis;
		if(parent!=null){
			parent.markFinalEnd(finalMillis);
		}
	}

	public long getAfterPeriod(){
		if(parent==null) {
			return finalEnd - this.createMillis;
		}
		
		MonitorPoint next = getNext(this);
		if(next==null)return 0;
		return next.createMillis - this.createMillis;
	}
	
	public long getSelfPeriod(){
		long self = getAfterPeriod();
		if(isLeaf()) return self;
		MonitorPoint mp1 = children.get(0);
		return self - (finalEnd - mp1.createMillis);
	}
	
	public long getBranchElipsedByEndTag(){
		if(isLeaf()){//is leaf
			return 0;
		}
		return finalEnd - this.createMillis;
	}
	
	public MonitorPoint getLastChild(){
		if(children==null|| children.size()==0)return null;
		int k = children.size();
		return  children.get(k-1);
	}

	public boolean isLeaf(){
		return children==null|| children.size()==0;
	}
	/**
	 * 
	 * @param current
	 * @return
	 */
	public static MonitorPoint getVeryEnd(MonitorPoint current){
		MonitorPoint lastChild = current.getLastChild();
		return (lastChild==null)? current : getVeryEnd(lastChild);
	}
	
	private static MonitorPoint getNext(MonitorPoint current){
		if(current==null)
			throw new IllegalArgumentException("shouldn't be null");
		if(current.parent == null)return null;//this is a root record.
		
		MonitorPoint next = null;
		int parentChildrenSize = current.parent.children.size();
		if(current.index+1 >= parentChildrenSize){// this is the last one of parent child
			next = getNext(current.parent);
		}else{
			next = current.parent.children.get(current.index+1);
		}
		return next;
	}
	
}
