/**
 * 
 */
package org.zmonitor.util.eventbus;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultEventTypeDefinition<ET> implements EventTypeDefinition<ET>{
	public static final Pattern PTN = Pattern.compile("on[A-Z][\\w$]*");
	private final Class<ET> evtType;
	/**
	 * 
	 * @param evtType
	 */
	public DefaultEventTypeDefinition(Class<ET> evtType) {
		super();
		this.evtType = evtType;
	}
	
	
	public Class<ET> getBaseEventType() {
		return evtType;
	}
	
	
	@SuppressWarnings("unchecked")
	public Class<? extends ET> resolveEventClass(Object event) {
		return (Class<? extends ET>) event.getClass();
	}
	
	
	@SuppressWarnings("unchecked")
	public Class<ET> retrieveListeningEventType(Method method) {
		if(!PTN.matcher(method.getName()).matches())return null;
		Class<?> clz = method.getParameterTypes()[0];
		try {
			clz.asSubclass(evtType);
			return (Class<ET>) clz;
		} catch (ClassCastException e) {
			return null;
		}
	}
}
