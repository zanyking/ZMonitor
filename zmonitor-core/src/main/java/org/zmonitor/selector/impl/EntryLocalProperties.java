/**
 * 
 */
package org.zmonitor.selector.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.SelectorEvalException;
import org.zmonitor.selector.UndefinedAttributeException;
import org.zmonitor.selector.impl.model.Attribute;
import org.zmonitor.selector.impl.model.PseudoClass;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;
import org.zmonitor.util.Objects;

/**
 * A collection of utilities that check local properties of Components. 
 * Including type, ID, class, attribute, and pseudo class.
 * @author simonpai, Ian YT Tsai(Zanyking)
 */
public class EntryLocalProperties {
	
	/**
	 * Returns true if the selector matches the given component. Combinators 
	 * are not allowed.
	 * @param component
	 * @param selector
	 * @return
	 */
	public static<T> boolean match(Entry<T> component, String selector) {
		return match(component, selector, null);
	}
	
	/**
	 * Returns true if the selector matches the given component. Combinators 
	 * are not allowed. 
	 * @param component
	 * @param selector
	 * @param defs
	 * @return
	 */
	public static<T> boolean match(Entry<T> component, String selector,
			Map<String, PseudoClassDef> defs) {
		List<Selector> selectorList = new Parser().parse(selector);
		MatchCtx<T> ctx = new MatchCtxImpl<T>(component, selectorList);
		for(Selector s : selectorList) {
			if(s.size() > 1) continue;
			if(match(ctx, s.get(0), defs)) return true;
		}
		return false;
	}
	
	/*package*/ static<T> boolean match(MatchCtx<T> context, 
			SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs){
		Entry<T> entry = context.getEntry();
		return matchType(entry, seq.getType()) 
			&& matchID(entry, seq.getId()) 
			&& matchClasses(entry, seq.getClasses()) 
			&& matchAttributes(entry, seq.getAttributes()) 
			&& matchPseudoClasses(context, seq.getPseudoClasses(), defs);
	}
	
	/*package*/ static<T> boolean matchID(Entry<T> component, String id){
		if(id == null) return true;
		return id.equals(component.getId());
	}
	
	/*package*/ static<T> boolean matchType(Entry<T> entry, String type){
		if(type == null) return true;
		return entry.getType().equals(type);
	}
	
	/*package*/ static<T> boolean matchClasses(Entry<T> entry, 
			Set<String> classes){
		if(classes == null || classes.isEmpty()) return true;

		Set<String> enClzes = entry.getConceptualCssClasses();
		
		return enClzes.containsAll(classes);
		
//		String scls = ((HtmlBasedComponent) component).getSclass();
//		String zcls = ((HtmlBasedComponent) component).getZclass();
//		for(String c : classes){
//			//this entry has no scls. 
//			if(scls == null) return false;
//			//looking for any 'C' which neither contained in scls nor equivalent to zcls. 
//			if(!scls.matches("(?:^|.*\\s)"+c+"(?:\\s.*|$)") && 
//					!Objects.equals(zcls, c)){
//				return false;	
//			}
//		}
//		return true;
	}
	
	
	
	
	/*package*/ static<T> boolean matchAttributes(Entry<T> entry, 
			List<Attribute> attributes){
		if(attributes == null || attributes.isEmpty()) return true;
		
		for(Attribute attr : attributes)
			if(!matchValue(getValue(entry, attr.getName()), attr)) 
				return false;
		
		return true;
	}
	
	/*package*/ static<T> boolean matchPseudoClasses(
			MatchCtx<T> context, List<PseudoClass> pseudoClasses, 
			Map<String, PseudoClassDef> defs){
		if(pseudoClasses == null || pseudoClasses.isEmpty()) return true;
		
		for(PseudoClass pc : pseudoClasses){
			PseudoClassDef def = getPseudoClassDef(defs, pc.getName());
			if(def == null) throw new SelectorEvalException(
					"Pseudo class definition not found: " + pc.getName());
			
			String[] param = pc.getParameter();
			if(param == null? 
					!def.accept(context) : 
				!def.accept(context, pc.getParameter())){ 
				return false;
			}
				
		}
		return true;
	}
	
	
	
	// helper //
	private static<T> T getValue(Entry<T> entry, String name){
		
		Class<?> clz = entry.getValue().getClass();
		
		try {
			return (T) clz.getMethod(
					"get"+capitalize(name)).invoke(entry);
		} catch (NoSuchMethodException e) {
			// no such method
		} catch (SecurityException e) {
			// SecurityManager doesn't like you
		} catch (IllegalAccessException e) {
			// attempted to call a non-public method
		} catch (InvocationTargetException e) {
			// exception thrown by the getter method
		}
		try {
			return (T) clz.getMethod(
					"is"+capitalize(name)).invoke(entry);
		} catch (NoSuchMethodException e) {
			// no such method
		} catch (SecurityException e) {
			// SecurityManager doesn't like you
		} catch (IllegalAccessException e) {
			// attempted to call a non-public method
		} catch (InvocationTargetException e) {
			// exception thrown by the getter method
		}
		
		// In ZK's internal implementation, we should search dynamic attributes 
		// after object's attribute look up, but now we just throw exception.
		throw new UndefinedAttributeException(clz, name);
//		return component.getAttribute(name);
	}
	
	private static String capitalize(String str){
		char first = str.charAt(0);
		if(Character.isUpperCase(first))return str;
		return Character.toUpperCase(first) + str.substring(1);
	}
	
	private static PseudoClassDef getPseudoClassDef(
			Map<String, PseudoClassDef> defs, String className) {
		PseudoClassDef def = null;
		if(defs != null && !defs.isEmpty())
			def = defs.get(className);
		if(def != null) return def;
		return BasicPseudoClassDefs.getDefinition(className);
	}
	
	private static Object parseData(String source, Class<?> expectedType){
		// TODO: enhance type support
		if(expectedType.equals(Integer.class)) return new Integer(source);
		if(expectedType.equals(Boolean.class)) return new Boolean(source);
		if(expectedType.equals(Double.class))  return new Double(source);
		return source;
	}
	
	private static boolean matchValue(Object value, Attribute attr){
		switch(attr.getOperator()){
		case BEGIN_WITH:
			return value!=null && value.toString().startsWith(attr.getValue());
		case END_WITH:
			return value!=null && value.toString().endsWith(attr.getValue());
		case CONTAIN:
			return value!=null && value.toString().contains(attr.getValue());
		case EQUAL:
		default:
			try {
				Object attrValue = parseData(attr.getValue(), 
						attr.isQuoted()? String.class : value.getClass());
				return Objects.equals(value, attrValue);
			} catch (Exception e) {
				// failed to convert attribute value to expected type
				return false;
			}
		}
	}
	
}
