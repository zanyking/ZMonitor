/**RegisteredMessageControl.java
 * 2011/10/15
 * 
 */
package org.zmonitor.web.test;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Callback;
import org.zmonitor.message.Message;
import org.zmonitor.message.MessageFailover;
import org.zmonitor.message.MessageHandler;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class RegisteredMessageManager {
	
	private final Map<UUID, Callback> callbackStore = 
		Collections.synchronizedMap(new HashMap<UUID, Callback>());
	/**
	 * 
	 * @param mesg
	 * @param callback
	 * @return
	 */
	public Message register(Message mesg, Callback callback){
		if(callback==null)return mesg;
		UUID uuid = UUID.randomUUID();
		callbackStore.put(uuid, callback);
		return new RegisteredMessage(uuid, mesg);
	}
	/**
	 *  handle any Registered message's response, simply return if the message is not registered before.
	 *  
	 * @param respMesg
	 * @param e
	 */
	public void acknowledge(Message respMesg, Exception e){
		RegisteredMessage regMesg = null;
		if(respMesg instanceof RegisteredMessage){
			regMesg = (RegisteredMessage) respMesg;
		}else{
			return;
		}
		Callback callback = callbackStore.remove(regMesg.uuid);
		if(callback==null){
			ZMLog.warn("a response come from master " +
					"suppose to get calback but failed: ticket="+regMesg.uuid);
			return;
		}
		try{
			if(e==null && !regMesg.isException()){
				callback.onSuccess(regMesg.getMessage());	
			}else if(callback instanceof MessageFailover){
				if(e==null)
					e = regMesg.getException();
				((MessageFailover)callback).onError(e);
			}
		}catch(Exception ex){
			ZMLog.warn(ex);// none of the error should throw up.
		}
	}
	/**
	 * Generate an acknowledgment of receipt which will send back to Client.
	 * 
	 * @param req the Request Message that was received from communication channel. 
	 * @param respObject the content that need to be send back. 
	 * @return null if the request message is not a Registered Message, otherwise an AR returned.
	 */
	public static Message generateAR(Message req, Serializable respObject){
		if(req instanceof RegisteredMessage){
			RegisteredMessage regMesg = (RegisteredMessage) req;
			return new RegisteredMessage(regMesg.uuid, respObject);
		}
		else return null;
	}
	/**
	 * 
	 * @param req
	 * @param handle
	 * @return
	 * @throws Exception 
	 */
	public static Message invoke(Message req, MessageHandler handle) throws Exception{
		Message ori = req;
		boolean hasCallback = false;
		if(req instanceof RegisteredMessage){
			RegisteredMessage reqMsg = ((RegisteredMessage)req);
			ori = reqMsg.getMessage();
			hasCallback = reqMsg.uuid != null;
		}
		return handle.handle(ori, hasCallback);
	}
	
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class RegisteredMessage extends Message{
		private static final long serialVersionUID = 8147428986789854419L;
		
		final Serializable object;
		final UUID uuid;
		/**
		 * 
		 * @param uuid
		 * @param object
		 */
		public RegisteredMessage(UUID uuid, Serializable object) {
			this.object = object;
			this.uuid = uuid;
		}
		public boolean isException(){
			return object instanceof Exception;
		}
		public Message getMessage() {
			return (Message) object;
		}
		public Exception getException(){
			return (Exception) object;
		}
	}

}
