/**
 * 
 */
package org.zmonitor.selector.impl.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A Finite State Machine implementation. Easier to used then ones described in
 * textbooks.
 * @author simonpai
 */
public abstract class StateMachine<E, C, IN> {
	
	// TODO: support getStates
	// TODO: support debug message level
	
	protected Map<E, StateCtx<E, C, IN>> _states;
	protected E _current;
	protected boolean _run;
	protected int _step;
	protected boolean _debug;
	
	public StateMachine(){
		_states = new HashMap<E, StateCtx<E, C, IN>>();
		_debug = false;
		init();
		reset();
	}
	
	
	
	// system /
	public StateMachine<E, C, IN> setDebugMode(boolean mode){
		_debug = mode;
		return this;
	}
	
	// definition //
	public StateCtx<E, C, IN> setState(E token, StateCtx<E, C, IN> state){
		if(state == null) throw new IllegalArgumentException(
				"State cannot be null. Use removeState() to remove a state.");
		_states.put(token, state.setMaster(this));
		return state;
	}
	
	public StateCtx<E, C, IN> removeState(E token){
		return _states.remove(token).setMaster(null);
	}
	
	public StateCtx<E, C, IN> getState(E token){
		return getState(token, true);
	}
	
	public StateCtx<E, C, IN> getState(E token, boolean autoCreate){
		StateCtx<E, C, IN> result = _states.get(token);
		if(result == null && autoCreate) 
			_states.put(token, result = new StateCtx<E, C, IN>().setMaster(this));
		return result;
	}
	
	protected void init(){}
	protected abstract E getLandingState(IN input, C inputClass);
	protected abstract C getClass(IN input);
	
	// event handler //
	protected void onReset(){}
	protected void onStart(IN input, C inputClass, E landing){}
	protected void onBeforeStep(IN input, C inputClass){}
	protected void onAfterStep(IN input, C inputClass, E origin, E destination){}
	protected void onStop(boolean endOfInput){}
	
	protected void onReject(IN input){
		throw new StateMachineException(_step, _current, input);
	}
	
	protected void onDebug(String message){
		System.out.println(message);
	}
	
	
	
	// operation //
	public final void run(Iterator<IN> inputs){
		_run = true;
		while(_run && inputs.hasNext())
			run(inputs.next());
		
		boolean endOfInput = !inputs.hasNext();
		onStop(endOfInput);
		if(_current != null)
			getState(_current).onStop(endOfInput);
		doDebug("");
		doDebug("Stop");
		doDebug("");
	}
	
	public final void run(IN input){
		
		C inputClass = getClass(input);
		
		doDebug("");
		doDebug("Step " + _step);
		doDebug("* Input: " + input + " (" + inputClass + ")");
		
		onBeforeStep(input, inputClass);
		
		final E origin = _current;
		E destination = null;
		
		if(inputClass == null) {
			doReject(input);
			return;
		}
		if(origin == null){
			destination = getLandingState(input, inputClass); // dest
			if(destination == null) {
				doReject(input);// reject if there's no destination.
				return;
			}
			onStart(input, inputClass, destination);
			getState(destination).onLand(input, inputClass, origin);
			
		} else {
			StateCtx<E, C, IN> state = getState(origin);
			
			if(state.isLeaving(input, inputClass)) {
				destination = state.getDestination(input, inputClass); // dest
				if(destination == null) {
					doReject(input);
					return;
				}
				state.onLeave(input, inputClass, destination);
				state.doTransit(input, inputClass);
				getState(destination).onLand(input, inputClass, origin);
				
			} else if(state.isReturning(input, inputClass)) {
				destination = origin; // dest
				state.onReturn(input, inputClass);
				
			} else { // rejected by state
				state.onReject(input, inputClass);
				doReject(input);
				return;
			}
		}
		
		_current = destination;
		
		doDebug("* State: " + origin + " -> " + destination);
		
		onAfterStep(input, inputClass, origin, destination);
		_step++;
	}
	
	public final void start(Iterator<IN> inputs){
		reset();
		run(inputs);
	}
	
	public final void start(IN input){
		reset();
		run(input);
	}
	
	public final void suspend(){
		_run = false;
	}
	
	public final void terminate(){
		reset();
	}
	
	
	
	// status query //
	// TODO: enhance
	public E getCurrentState(){
		return _current;
	}
	
	public boolean isTerminated(){
		return !_run && _current == null;
	}
	
	public boolean isSuspended(){
		return !_run && _current != null;
	}
	
	
	
	// default internal operation //
	protected final void doReject(IN input){
		_run = false;
		onReject(input);
	}
	
	protected final void doDebug(String message){
		if(_debug) onDebug(message);
	}
	
	private final void reset(){
		_current = null;
		_run = false;
		_step = 0;
		doDebug("");
		doDebug("Reset");
		onReset();
	}
	
	/*package*/ final void terminateAt(IN input){
		getState(_current).onLeave(input, null, null);
		reset();
	}
	
	
	
	// exception //
	public static class StateMachineException extends RuntimeException {
		private static final long serialVersionUID = -6580348498729948101L;
		
		private int _step;
		private Object _state;
		private Object _input;
		
		public StateMachineException(int step, Object state, Object input){
			this(step, state, input, "Rejected at step " + step + 
					" with current state: " + state + ", input: " + input);
		}
		
		public StateMachineException(int step, Object state, Object input, 
				String message){
			super(message);
			_step = step;
			_state = state;
			_input = input;
		}
		
		public StateMachineException(String message){
			super(message);
		}
		
		public int getStep(){
			return _step;
		}
		
		public Object getState(){
			return _state;
		}
		
		public Object getInput(){
			return _input;
		}
	}
	

}
