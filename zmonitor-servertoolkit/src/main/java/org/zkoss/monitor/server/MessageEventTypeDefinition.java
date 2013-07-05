/**MessageEventTypeDefinition.java
 * 2012/1/12
 * 
 */
package org.zkoss.monitor.server;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Message;
import org.zmonitor.util.eventbus.DefaultEventTypeDefinition;
import org.zmonitor.util.eventbus.EventTypeDefinition;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MessageEventTypeDefinition  implements EventTypeDefinition<Message>{
	public Class<Message> getBaseEventType() {
		return Message.class;
	}
	@SuppressWarnings("rawtypes")
	public Class<? extends Message> resolveEventClass(Object event) {
		MessageEvent mEvt = (MessageEvent) event;
		return mEvt.getMessage().getClass();
	}
	@SuppressWarnings("unchecked")
	public Class<Message> retrieveListeningEventType(Method method) {
		if(!DefaultEventTypeDefinition.PTN.matcher(method.getName()).matches())return null;
		Class<?> pc = method.getParameterTypes()[0];
		if(!pc.equals(MessageEvent.class)){
			return null;
		}
		Type pt = method.getGenericParameterTypes()[0];
		if(pt instanceof ParameterizedType){
			ParameterizedType ppType = (ParameterizedType) pt;
			Type cls = ppType.getActualTypeArguments()[0];
			if(cls instanceof Class<?>){
				return (Class<Message>) cls;
			}else{
				ZMLog.debug("getActualTypeArguments() didn't return a Class<?>: ",cls);
			}
		}else{
			ZMLog.debug("NOT a ParameterizedType: ",pt);
		}
		
		return null;
	}
}
