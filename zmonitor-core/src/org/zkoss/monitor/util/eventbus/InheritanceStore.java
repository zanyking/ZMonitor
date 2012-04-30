/**SuperTypeFinder.java
 * 2011/7/18
 * 
 */
package org.zkoss.monitor.util.eventbus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class InheritanceStore<B> {

	private final Map<Class<? extends B>, List<Class<? extends B>>> store =
			new HashMap<Class<? extends B>, List<Class<? extends B>>>();
	
	private final Class<?> baseClass;
	
	public InheritanceStore() {
		this.baseClass =  Object.class;
	}
	/**
	 * 
	 * @param baseClass
	 */
	public InheritanceStore(Class<B> baseClass) {
		this.baseClass = baseClass;
	}

	/**
	 * 
	 * @param cls
	 * @return
	 */
	public List<Class<? extends B>> find( Class<? extends B> cls){
		ArrayList<Class<? extends B>> result = new ArrayList<Class<? extends B>>(); 
		result.addAll(find0(cls));
		return result;
	}
	private List<Class<? extends B>> find0( Class<? extends B> cls){
		List<Class<? extends B>> records = store.get(cls);
		if(records!=null)return records;
		
		store.put(cls, records = new ArrayList<Class<? extends B>>());
		
		LinkedHashSet<Class<? extends B>> set = new LinkedHashSet<Class<? extends B>>();
		
		set.add(cls);//add this 
		
		
		Type[] interfaces = cls.getGenericInterfaces();
		for(Type type : interfaces){
			if(type instanceof Class){
				set.addAll(find0((Class<? extends B>) type));
			}else if(type instanceof ParameterizedType){
				ParameterizedType t = (ParameterizedType) type;
				set.addAll(find0((Class<? extends B>) t.getRawType()));
				
			}else{
				throw new IllegalStateException("I cannot get a proper class presentation from type: "+type);
			}
			
		}
		
		Class<?> superCls = cls.getSuperclass();
		if(superCls==null ){
			records.addAll(set);
			return records;
		}
		try{
			superCls.asSubclass(baseClass);	
		}catch(ClassCastException  e){
			records.addAll(set);
			return records;
		}
		//recursive...	
		set.addAll(find0((Class<? extends B>) superCls));
		records.addAll(set);
		return records;
	}
	
	
	public static interface D{}
	public static interface E extends G, H, I{}
	public static interface F{}
	public static interface G extends J{}
	public static interface H extends J{}
	public static interface I extends J{}
	public static interface J{}
	public static interface K{}
	public static interface L{}
	
	public static class C1 extends C2
	implements D, E
	{}
	
	public static class C2 extends C3
	implements G, F
	{}
	
	public static class C3 extends C4
	implements J, K, L
	{}
	
	public static class C4 extends C5
	{}
	
	public static class C5
	{}
	
	public static void main(String[] args) {
		InheritanceStore<C5> finder = 
			new InheritanceStore<C5>(C5.class);
		
		for(Class<?> cls : finder.find(C1.class))
			System.out.println(cls.getSimpleName());
		
		System.out.println("-----------------------");
		for(Class<?> cls : finder.find(C3.class))
			System.out.println(cls.getSimpleName());
		
		System.out.println("-----------------------");
		InheritanceStore<Object> finder2 = 
			new InheritanceStore<Object>(Object.class);
		for(Class<?> cls : finder2.find(D.class))
			System.out.println(cls.getSimpleName());
		System.out.println("-----------------------");
		for(Class<?> cls : finder2.find(E.class))
			System.out.println(cls.getSimpleName());
	}
	
	
}
