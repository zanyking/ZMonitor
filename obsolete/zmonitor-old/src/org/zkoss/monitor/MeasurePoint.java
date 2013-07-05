/**
 * 2011/3/4
 * 
 */
package org.zkoss.monitor;

import java.io.Serializable;
import java.util.ArrayList;

import org.zkoss.monitor.spi.Name;




/**
 * 
 * 
 * @author Ian YT Tsai(Zanyking)
 */
public class MeasurePoint implements Serializable{
	private static final long serialVersionUID = 1772552143735347953L;
	
	public Timeline timeline;
	public long createTime;
	public long tickPeriod;
	public int stack;
	public int index;
	public MeasurePoint parent;
	public ArrayList<MeasurePoint> children;	
	
	public Name name;
	public String message;
	public long finalEnd;// Measure Point
	
	/**
	 * 
	 * @param parent
	 * @param mesg
	 */
	public MeasurePoint(Name name, String mesg, MeasurePoint parent, boolean isLeaf, Timeline bb, long createTime) {
		
		timeline = bb;
		timeline.increament();
		this.createTime = createTime;
		this.name = name;
		this.message = mesg;//TODO: has potential to be an object...
		this.parent = parent;
		this.stack = (parent==null) ? 0 : parent.stack+1;
		
		this.children = isLeaf ? null : new ArrayList<MeasurePoint>(10);
		MeasurePoint previousSibling = (parent==null) ?  
				null : parent.getLastChild();
		if(previousSibling==null){
			if(parent!=null){
				tickPeriod = createTime - parent.createTime;
			}else{
				tickPeriod = 0;	
			}
		}else{
			tickPeriod = createTime - previousSibling.createTime;
		}
		if(parent!=null){
			index = parent.children.size(); 
			parent.children.add(this);
		}else{
			index = 0;
		}
	}

	public void markFinalEnd(long finalMillis) {
		finalEnd = finalMillis;
		if(parent!=null){
			parent.markFinalEnd(finalMillis);
		}
	}

	public long getAfterPeriod(){
		if(parent==null) {
			return finalEnd - this.createTime;
		}
		
		MeasurePoint next = getNext(this);
		if(next==null)return 0;
		return next.createTime - this.createTime;
	}
	
	public long getSelfPeriod(){
		long self = getAfterPeriod();
		if(isLeaf()) return self;
		MeasurePoint mp1 = children.get(0);
		return self - (finalEnd - mp1.createTime);
	}
	
	public long getBranchElipsedByEndTag(){
		if(isLeaf()){//is leaf
			return 0;
		}
		return finalEnd - this.createTime;
	}
	
	public MeasurePoint getLastChild(){
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
	public static MeasurePoint getVeryEnd(MeasurePoint current){
		MeasurePoint lastChild = current.getLastChild();
		return (lastChild==null)? current : getVeryEnd(lastChild);
	}
	
	private static MeasurePoint getNext(MeasurePoint current){
		if(current==null)
			throw new IllegalArgumentException("shouldn't be null");
		if(current.parent == null)return null;//this is a root record.
		
		MeasurePoint next = null;
		int parentChildrenSize = current.parent.children.size();
		if(current.index+1 >= parentChildrenSize){// this is the last one of parent child
			next = getNext(current.parent);
		}else{
			next = current.parent.children.get(current.index+1);
		}
		return next;
	}
	
}
