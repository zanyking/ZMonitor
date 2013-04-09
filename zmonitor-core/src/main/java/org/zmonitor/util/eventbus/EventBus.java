/**EventBus.java
 * 2011/7/18
 * 
 */
package org.zmonitor.util.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 
 * A generic type of Event Bus concept implementation.<br>
 * 
 * It accept Event by Event baseType (a Java Class), and EventListener by naming convention 
 * (the method name has a match to regex: on[A-Z][\w$]*).<br>
 * <ol>
 * 	<li> Currently there's no post method to Event Bus, if you want one, you have to wrap it your self.
 * 	<li> This class is implemented in a thread safe manner, because this is based on channel concept 
 * and there might be several bands on transmission at the same time.  
 *  <li>
 * </ol>
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
@SuppressWarnings("unchecked")
public class EventBus<ET> {

	private final InheritanceStore<ET> evtSuTypeStore;
	
	
	private final Map<Class<ET>, Map<Invoker, Set<Object>>> listenerStore = 
		Collections.synchronizedMap(new HashMap<Class<ET>, Map<Invoker, Set<Object>>>());
	
	private final DisposeManager disposeManager = new DisposeManager();
	
	private final EventTypeDefinition<ET> eventTypeDef;
	
	/**
	 * 
	 * @param evtType
	 */
	public EventBus(Class<ET> evtType){
		this(new DefaultEventTypeDefinition<ET>(evtType));
	}

	/**
	 * @param evtTypeDef
	 */
	public EventBus(EventTypeDefinition<ET> evtTypeDef){
		this.eventTypeDef = evtTypeDef;
		this.evtSuTypeStore = 
			new InheritanceStore<ET>(evtTypeDef.getBaseEventType());
	}
	
	
	/**
	 * 
	 * 						
	 * 
	 * @param evt
	 * @throws Exception
	 */
	public void send( Object evt)throws Exception{
		disposeManager.cleanUp();
		List<Class<? extends ET>> clz = null;
		
		synchronized(evtSuTypeStore){
			clz = evtSuTypeStore.find(eventTypeDef.resolveEventClass(evt)); 
		}
		
		for(Class<?> cls : clz){
			Map<Invoker, Set<Object>> invocations = listenerStore.get(cls);
			if(invocations==null)continue;
			for(Map.Entry<Invoker, Set<Object>> entry :  
				new ArrayList<Map.Entry<Invoker, Set<Object>>>(invocations.entrySet())){
				
				Invoker invoker = entry.getKey();
				for(Object listeningMethodHolder : new ArrayList<Object>(entry.getValue())){
					//TODO: use different thread, make it thread safe...
					//TODO: how to detect the given event "is" the 
					invoker.invoke(listeningMethodHolder, evt);
				}
			}
		}
	}
	
	/**
	 * 
	 * event listener is the owner class of Event Listening methods, 
	 * a method that can be considered as a Event Listening method need to satisfy this conditions:<br>
	 * <ol>
	 * 	<li> Need to be a public method.
	 * 	<li> 
	 * </ol>
	 * @param evtListener
	 */
	public void listen(Object evtListener){
		
		boolean disposable = (evtListener instanceof Disposable);
		if(disposable && ((Disposable)evtListener).isDisposable())
			return;
		
		for(Invoker invoker: getListenerInvokers(evtListener)){
			
			Map<Invoker, Set<Object>> invocations = null;
			synchronized(listenerStore){// lazy initialization...
				invocations = listenerStore.get(invoker.evtType);
				if(invocations==null){
					listenerStore.put((Class<ET>) invoker.evtType, 
						invocations = new HashMap<EventBus.Invoker, Set<Object>>());
				}
			}
			
			
			Set<Object> listeningMethodHolders = null;
			synchronized(invocations){// lazy initialization...
				listeningMethodHolders = invocations.get(invoker);
				if(listeningMethodHolders==null){
					invocations.put(invoker, 
							listeningMethodHolders = new LinkedHashSet<Object>());
				}	
			}
			
			listeningMethodHolders.add(evtListener);
			if(disposable){
				disposeManager.add((Disposable) evtListener, listeningMethodHolders);
			}
		}
	}
	

	/**
	 * 
	 * @param evtListener
	 */
	public void unlisten(Object evtListener){
		Class<ET> liCls = (Class<ET>) evtListener.getClass();
		
		for(Invoker invoker: getListenerInvokers(liCls)){
			Map<Invoker, Set<Object>> invocations = listenerStore.get(invoker.evtType);
			if(invocations==null){
				continue;
			}
			Set<Object> listeners = invocations.get(invoker);
			if(listeners==null){
				continue;
			}
			listeners.remove(evtListener);
		}
	}
	
	
	private final Map<Class<?>, List<Invoker>> liMethods = 
		new HashMap<Class<?>, List<Invoker>>();
	
	private Collection<Invoker> getListenerInvokers(Object evtListener){
		Class<ET> liCls = (Class<ET>) evtListener.getClass();

		List<Invoker> evtLiMethods = null;
		synchronized(liMethods){
			evtLiMethods = liMethods.get(liCls);
			if(evtLiMethods==null){
				evtLiMethods = new ArrayList<Invoker>();
				for(Method m : liCls.getMethods()){
					if(!m.getReturnType().equals(Void.TYPE))continue;//must be like this 
					if(m.getParameterTypes().length!=1)continue;//"Event is an Object" so must be single.
					
					Class<ET> cls = (Class<ET>) eventTypeDef.retrieveListeningEventType(m);
					if(cls==null)continue;
					evtLiMethods.add(new Invoker(m,cls));
				}
				liMethods.put(liCls, evtLiMethods);
			}	
		}
		return new ArrayList<Invoker>(evtLiMethods);
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class Invoker{
		final Method method;
		final Class<?> evtType;
		public Invoker(Method method, Class<?> evtType) {
			super();
			if(method==null || evtType==null)
				throw new IllegalArgumentException("method & evtType cannot be NULL!");
			this.method = method;
			this.evtType = evtType;
		}
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((evtType == null) ? 0 : evtType.hashCode());
			result = prime * result
					+ ((method == null) ? 0 : method.hashCode());
			return result;
		}
		public boolean equals(Object obj) {
			if (this == obj)return true;
			if (obj == null)return false;
			Invoker other = (Invoker) obj;
			if (evtType != other.evtType)
				return false;
			if (method!=other.method)
				return false;
			
			return true;
		}
		public void invoke(Object instance, Object evt)throws Exception{
			try{
				boolean did = false;
				if(!method.isAccessible()){
					method.setAccessible(did = true);
					//TODO: is there any way to do things like this without fuck up the class?	
				}
				method.invoke(instance, evt);	
			}finally{
				//TODO: don't want to do this, too costly
//				if(did)method.setAccessible(false);
			}
		}
	}//end of class...
	
	
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class DisposeManager{
		private final Map<Disposable, Cleaner> cleaners = 
			Collections.synchronizedMap(new HashMap<Disposable, Cleaner>());
		/**
		 * 
		 * @param listener
		 * @param listeners
		 */
		public synchronized void add(Disposable listener, Collection<Object> listeners){
			Cleaner cleaner = cleaners.get(listener);
			if(cleaner==null){
				cleaners.put(listener, 
					cleaner = new Cleaner(listener));
			}
			cleaner.stores.add(listeners);
		}
		/**
		 * 
		 */
		public synchronized void cleanUp(){//TODO: use AsyncExecutor to re-think this impl...
			if(cleaners.size()==0)return;
			for(Cleaner cleaner : 
				new ArrayList<Cleaner>(cleaners.values())){
				if(cleaner.clean())
					cleaners.remove(cleaner);	
			}
		}
	}//end of class...
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class Cleaner{
		final Disposable listener;
		final ArrayList<Collection<Object>> stores;
		
		public Cleaner(Disposable listenerInst) {
			this.listener = listenerInst;
			this.stores = new ArrayList<Collection<Object>>();
		}
		public boolean clean(){
			if(!listener.isDisposable())return false;
			for(Collection<Object> store: stores){
				store.remove(listener);
			}
			return true;
		}
	}//end of class...
}
