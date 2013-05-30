/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.ArrayList;
import java.util.List;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class Selection {

	protected final List<Entry> entries;
	
	/**
	 * 
	 */
	protected Selection(List list) {
		
		if(list.isEmpty()|| list.get(0) instanceof Entry){
			
			this.entries = list;
		
		}else if(list.get(0) instanceof EntryContainer){
			entries = new ArrayList<Entry>(list.size());
			EntryContainer container; 
			for(Object obj : list){
				container = (EntryContainer) obj;
				entries.add( container.getFirstRoot());
			}
		}else{
			throw new IllegalArgumentException(" the given List is not correct.");	
		}
	}
	/**
	 * 
	 * @param entry
	 */
	protected Selection(Entry entry) {
		entries = new ArrayList<Entry>(1);
		entries.add(entry);
	}
	/**
	 * 
	 * @param container
	 */
	protected Selection(EntryContainer container) {
		entries = new ArrayList<Entry>(1);
		entries.add(container.getFirstRoot());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Entry> getEntries(){
		return entries;
	}
	/**
	 * 
	 * @return
	 */
	public Entry getFirstEntry(){
		return entries.get(0);
	}
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		return entries.isEmpty();
	}
	/**
	 * 
	 * @param selector
	 * @return
	 */
	public Selection select(String selector){
		ArrayList<Entry> arr = new ArrayList<Entry>();
		for(Entry en : entries){
			arr.addAll(org.zmonitor.selector.Selectors.find(en, selector));
		}
		return new Selection(arr);
	}
	
	
}
