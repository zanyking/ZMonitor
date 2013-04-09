/**
 * 
 */
package org.zkoss.monitor.server;

import org.zmonitor.message.Message;
import org.zmonitor.message.MessageHandler;
import org.zmonitor.util.eventbus.EventBus;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class EventBusMessageHandler implements MessageHandler {
	
	private EventBus<Message> dispatcher = 
		new EventBus<Message>(new MessageEventTypeDefinition());
	
	/* (non-Javadoc)
	 * @see org.zkoss.monitor.message.MessageHandler#handle(org.zkoss.monitor.message.Message, boolean)
	 */
	public Message handle(Message request, boolean hasCallback)
			throws Exception {
		MessageEvent mEvent = new MessageEvent(request, hasCallback);
		dispatcher.send(mEvent);
		if(hasCallback)return mEvent.getResponse();
		return null;
	}
	/**
	 * 
	 * @param evtListener
	 */
	public void addMessageEventListener(Object evtListener){
		dispatcher.listen(evtListener);
	}
	/**
	 * 
	 * @param evtListener
	 */
	public void removeMessageEventListener(Object evtListener){
		dispatcher.unlisten(evtListener);
	}
	
	
	
	
}
