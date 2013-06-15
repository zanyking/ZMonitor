/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.Selector.Combinator;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Arguments;

/**
 * 
 * 
 * immutable object for state transition. <br>
 * is designed to record the state while traversing the entire entry tree.
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SelectorContext {
	final List<Selector> selectors;
	final int[] seqCurrentIdx;
	final Map<String, PseudoClassDef> defs;
	
	/**
	 * 
	 * @param selectors
	 */
	public SelectorContext(List<Selector> selectors, Map<String, PseudoClassDef> defs) {//Root
		Arguments.checkNotNull(selectors);
		Arguments.checkNotNull(defs);
		
		this.selectors = selectors;
		int selectorAmount = selectors.size();
		seqCurrentIdx = new int[selectorAmount];
		for(int i=0;i<seqCurrentIdx.length;i++){
			seqCurrentIdx[i] = -1;
		}
		this.defs = defs;
	}
	
	
	
	interface Transit{
		
	}
	
	/**
	 * cursor moves to next entry, compute and prepare a SelectorContext for this entry.
	 * @return
	 */
	public SelectorContext toFirstChild(Entry entry){
		//TODO
		return null;
	}
	
	public SelectorContext toNextSibling(){
		//TODO
		return null;
	}
	
	
	private SelectorContext toNewContext(Entry entry, Transit transit){

		Selector selector;
		SimpleSelectorSequence seq;
		
		//TODO
		//for each Selector
		for(int selIdx=0, selEnd=selectors.size(); 
				selIdx<selEnd; 
				selIdx++){
			
			selector = selectors.get(selIdx);
			//TODO
			//for each SelectorSequence of a Selector
			for(int seqIdx=seqCurrentIdx[selIdx], 
						seqEnd=selectors.get(selIdx).size(); 
					seqIdx < seqEnd; 
					seqIdx++){
				
				seq = selector.get(seqIdx);
				//TODO	
				EntryLocalProperties.match(entry, seq, defs);
				
			}
			//TODO
			
		}
		return null;
	}
	
	private void getSequenceHandler(Combinator cb){
		switch(cb){
		case DESCENDANT: 
		case CHILD: 
		case ADJACENT_SIBLING: 
		case GENERAL_SIBLING:
		}
	}
	
	/*
	 * DESCENDANT
	 * 
	 * .a .b .c
	 * 	
	 *  <div> [-1]
	 *    <div clz="a"> [ 0]
	 *      <div clz="b">[1]
	 * 	      <div >[1]
	 * 	        <div clz="c">[2]
	 *        <div clz="c">[2]
	 *        <div clz="d">[1]
	 * 	2 matches  
	 *  DESCENDANT_INIT: init Idx=-1, check matches,  
	 * 
	 * 
	 * CHILD
	 * .a > .b > .c
	 *  <div clz="a">[100]
	 * 	  <div clz="b">[110]
	 * 	    <div >[000]
	 * 	      <div clz="c">[000]
	 *        <div clz="a">[100]
	 *          <div clz="b">[110]
	 *      <div clz="c">[111]
	 *  1 match
	 * 
	 * 
	 * 
	 */
	
	
	

}
