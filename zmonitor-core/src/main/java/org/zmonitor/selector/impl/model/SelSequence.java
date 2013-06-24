/**
 * 
 */
package org.zmonitor.selector.impl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zmonitor.selector.impl.model.Attribute.Operator;
import org.zmonitor.selector.impl.model.Selector.Combinator;

/**
 * The model representing a sequence of simple selectors.
 * @author simonpai, Ian YT Tsai
 */
public class SelSequence {
	
	private Combinator _combinator;
	private String _type;
	private String _id;
	private Set<String> _classes;
	private List<Attribute> _attributes;
	private List<PseudoClass> _pseudoClasses;
	
	private Attribute _currAttribute;
	private PseudoClass _currPseudoClass;
	private final SelSequence _previous;
	private SelSequence _next;
	private int index;
	
	/**
	 * 
	 */
	private int inheritableIdx = -1;
	/**
	 * whether there's a match to this seq or not, the   
	 */
	private int transitableIdx = -1;
	
	
	/* package */SelSequence(SelSequence previous) {
		_combinator = Combinator.DESCENDANT;
		_classes = new HashSet<String>();
		_attributes = new ArrayList<Attribute>();
		_pseudoClasses = new ArrayList<PseudoClass>();
		_previous = previous;
		if (_previous != null) {// this sequence has previous.
			_previous.setNext(this);// maintain a double liked list to forward
									// and backward
			index = _previous.index+1;
			inheritableIdx = _previous.inheritableIdx;
			
//			int tempTransitableIdx =
			if(Direction.INHERIT.isCbInSameDirection(_previous)){
				transitableIdx = _previous.inheritableIdx;
			}else{
				transitableIdx = _previous.transitableIdx;
			}
//			if(transitableIdx<_previous.inheritableIdx){
//				
//			}
			
		}else{
			index = 0;
		}
	}
	
	
	public int getInheritableIdx() {
		return inheritableIdx;
	}


	public int getTransitableIdx() {
		return transitableIdx;
	}


	public int getIndex() {
		return index;
	}
	
	public void setToEnd(){
		if(inheritableIdx==index) inheritableIdx--;
		if(transitableIdx==index) transitableIdx--;
	}
	
	public boolean isEnd(){
		return this._next==null;
	}
	
	
	public void setNext(SelSequence next){
		if(Direction.INHERIT.isTransitive(_combinator))
			inheritableIdx = index;//lat decided...
		_next = next;
	}
	
	public SelSequence getNext(){
		return _next;
	}
	
	public SelSequence getPrevious(){
		return _previous;
	}
	
	// getter //
	public Combinator getCombinator(){
		return _combinator;
	}
	
	public String getType(){
		return _type;
	}
	
	public String getId(){
		return _id;
	}
	
	public Set<String> getClasses(){
		return Collections.unmodifiableSet(_classes);
	}
	
	public List<Attribute> getAttributes(){
		return Collections.unmodifiableList(_attributes);
	}
	
	public List<PseudoClass> getPseudoClasses(){
		return Collections.unmodifiableList(_pseudoClasses);
	}
	
	
	
	// setter //
	public void setCombinator(Combinator combinator){
		_combinator = combinator;
		if(Direction.INHERIT.isTransitive(combinator)){
			inheritableIdx = this.index;
		}else if(Direction.SIBLING.isTransitive(combinator)){
			transitableIdx = this.index;
		}
	}
	
	public void setType(String type){
		_type = type;
	}
	
	public void setId(String id){
		_id = id;
	}
	
	public void addClass(String clazz){
		if(!_classes.contains(clazz)) _classes.add(clazz);
	}
	
	public void addAttribute(String name){
		_attributes.add(_currAttribute = new Attribute(name));
	}
	
	public void attachAttributeOperator(Operator operator){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setOperator(operator);
	}
	
	public void attachAttributeValue(String value){
		attachAttributeValue(value, false);
	}
	
	public void attachAttributeValue(String value, boolean quoted){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setValue(value, quoted);
	}
	
	public void attachAttributeQuote(boolean inQuote){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setQuoted(inQuote);
	}
	
	public void addPseudoClass(String function){
		_pseudoClasses.add(_currPseudoClass = new PseudoClass(function));
	}
	
	public void attachPseudoClassParameter(String parameter){
		if(_currPseudoClass == null) throw new IllegalStateException();
		_currPseudoClass.addParameter(parameter);
	}
	
	public static int getIndex(SelSequence seq){
		return seq==null? -1 : seq.getIndex();
	}
	@Override
	public String toString() {
		if(_type == null && _id == null && _classes.isEmpty() && 
				_pseudoClasses.isEmpty() && _attributes.isEmpty()) return "*";
		
		StringBuffer sb = new StringBuffer(_type == null ? "" : _type.toString());
		
		if(_id != null) 
			sb.append("#").append(_id);
		
		if(!_classes.isEmpty()) 
			for(String c : _classes) 
				sb.append(".").append(c);
		
		if(!_pseudoClasses.isEmpty()) 
			for(PseudoClass p : _pseudoClasses)
				sb.append(p);
		
		if(!_attributes.isEmpty())
			for(Attribute a : _attributes)
				sb.append(a);
		
		return sb.toString();
	}
	
	public static String toStringWithCB(SelSequence seq){
		if(seq==null)return null;
		return seq+", CB:"+seq._combinator.name();
	}
	
	public static boolean greaterThan(SelSequence seq, SelSequence seq2){
		int seqIdx = seq==null? -1 : seq.getIndex();
		int seq2Idx = seq2==null? -1 : seq2.getIndex();
		
		return seqIdx > seq2Idx;
	}
	
	public static boolean reachedEnd(SelSequence seq){
		if(seq==null)return false;
		return seq.getNext()==null;
	}
}

