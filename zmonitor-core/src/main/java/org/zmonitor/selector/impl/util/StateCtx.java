/**
 * 
 */
package org.zmonitor.selector.impl.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A context for a State in a StateMachine, holding returning and transition
 * information.
 * @author simonpai
 */
public class StateCtx<E, C, IN> {
	
	protected StateMachine<E, C, IN> _machine;
	
	// local properties //
	protected boolean _returnAll;
	protected final Set<C> _returners;
	protected final Set<IN> _minorReturners;
	protected final Map<C, E> _transitions;
	protected final Map<C, TransitionListener<IN, C>> _transitionListeners;
	protected final Map<IN, E> _minorTransitions;
	protected final Map<IN, TransitionListener<IN, C>> _minorTransitionListeners;
	
	public StateCtx(){
		_returnAll = false;
		_returners = new HashSet<C>();
		_minorReturners = new HashSet<IN>();
		_transitions = new HashMap<C, E>();
		_transitionListeners = new HashMap<C, TransitionListener<IN, C>>();
		_minorTransitions = new HashMap<IN, E>();
		_minorTransitionListeners = new HashMap<IN, TransitionListener<IN, C>>();
		init();
	}
	
	protected StateCtx<E, C, IN> setMaster(StateMachine<E, C, IN> master){
		_machine = master;
		return this;
	}
	
	
	
	// definition //
	protected void init(){}
	
	// returning //
	public StateCtx<E, C, IN> addReturningClasses(C ... inputClasses){
		for(C c : inputClasses) _returners.add(c);
		return this;
	}
	
	public StateCtx<E, C, IN> addReturningClasses(Collection<C> collection){
		_returners.addAll(collection);
		return this;
	}
	
	public StateCtx<E, C, IN> addReturningInputs(IN ... inputs){
		for(IN i : inputs) _minorReturners.add(i);
		return this;
	}
	
	public StateCtx<E, C, IN> addReturningInputs(Collection<IN> collection){
		_minorReturners.addAll(collection);
		return this;
	}
	
	public StateCtx<E, C, IN> setReturningAll(boolean returnAll){
		_returnAll = returnAll;
		return this;
	}
	
	// transition //
	public StateCtx<E, C, IN> addTransition(C inputClass, E destination){
		return addTransition(inputClass, destination, null);
	}
	
	public StateCtx<E, C, IN> addTransition(C inputClass, E destination, 
			TransitionListener<IN, C> callback){
		_transitions.put(inputClass, destination);
		if(callback == null)
			_transitionListeners.remove(inputClass);
		else
			_transitionListeners.put(inputClass, callback);
		return this;
	}
	
	public StateCtx<E, C, IN> addTransitions(E destination, C ... inputClasses){
		return addTransitions(destination, null, inputClasses);
	}
	
	public StateCtx<E, C, IN> addTransitions(E destination, 
			TransitionListener<IN, C> callback, C ... inputClasses){
		for(C c : inputClasses)
			addTransition(c, destination, callback);
		return this;
	}
	
	// route //
	public StateCtx<E, C, IN> addRoute(C inputClass, E destination){
		return addRoute(inputClass, destination, null);
	}
	
	public StateCtx<E, C, IN> addRoute(C inputClass, E destination, 
			TransitionListener<IN, C> callback){
		addTransition(inputClass, destination, callback);
		return _machine.getState(destination);
	}
	
	public StateCtx<E, C, IN> addRoutes(E destination, C ... inputClasses){
		return addRoutes(destination, null, inputClasses);
	}
	
	public StateCtx<E, C, IN> addRoutes(E destination, TransitionListener<IN, C> callback, 
			C ... inputClasses){
		addTransitions(destination, callback, inputClasses);
		return _machine.getState(destination);
	}
	
	// minor transition //
	public StateCtx<E, C, IN> addMinorTransition(IN input, E destination){
		return addMinorTransition(input, destination, null);
	}
	
	public StateCtx<E, C, IN> addMinorTransition(IN input, E destination, 
			TransitionListener<IN, C> callback){
		_minorTransitions.put(input, destination);
		if(callback == null)
			_minorTransitionListeners.remove(input);
		else
			_minorTransitionListeners.put(input, callback);
		return this;
	}
	
	public StateCtx<E, C, IN> addMinorTransitions(E destination, IN ... inputs){
		return addMinorTransitions(destination, null, inputs);
	}
	
	public StateCtx<E, C, IN> addMinorTransitions(E destination, 
			TransitionListener<IN, C> callback, IN ... inputs){
		for(IN i : inputs)
			addMinorTransition(i, destination, callback);
		return this;
	}
	
	// minor route //
	public StateCtx<E, C, IN> addMinorRoute(IN input, E destination){
		return addMinorRoute(input, destination, null);
	}
	
	public StateCtx<E, C, IN> addMinorRoute(IN input, E destination, 
			TransitionListener<IN, C> callback){
		addMinorTransition(input, destination, callback);
		return _machine.getState(destination);
	}
	
	public StateCtx<E, C, IN> addMinorRoutes(E destination, IN ... inputs){
		return addMinorRoutes(destination, null, inputs);
	}
	
	public StateCtx<E, C, IN> addMinorRoutes(E destination, 
			TransitionListener<IN, C> callback, IN ... inputs){
		addMinorTransitions(destination, callback, inputs);
		return _machine.getState(destination);
	}
	
	
	
	// query //
	public boolean isReturningAll(){
		return _returnAll;
	}
	
	public boolean isReturning(IN input, C inputClass){
		return _returnAll || _returners.contains(inputClass) || 
			_minorReturners.contains(input);
	}
	
	public boolean isLeaving(IN input, C inputClass){
		return _transitions.containsKey(inputClass) || 
			_minorTransitions.containsKey(input);
	}
	
	public E getDestination(IN input, C inputClass){
		E result = _minorTransitions.get(input);
		return (result != null)? result : _transitions.get(inputClass);
	}
	
	
	
	// event handler //
	protected void onLand(IN input, C inputClass, E origin){}
	protected void onReturn(IN input, C inputClass){}
	protected void onReject(IN input, C inputClass){}
	protected void onLeave(IN input, C inputClass, E destination){}
	protected void onStop(boolean endOfInput){}
	
	public interface TransitionListener<IN, C> {
		public void onTransit(IN input, C inputClass);
	}
	
	
	
	// default operation //
	/*package*/ void doTransit(IN input, C inputClass){
		TransitionListener<IN, C> c = _transitionListeners.get(inputClass);
		if(c != null) c.onTransit(input, inputClass);
		c = _minorTransitionListeners.get(input);
		if(c != null) c.onTransit(input, inputClass);
	}
}
