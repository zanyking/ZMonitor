/**EvtBus_TEST.java
 * 2011/10/25
 * 
 */
package zmonitor.test.util;

import org.junit.Test;
import org.zmonitor.message.Message;
import org.zmonitor.message.NewTimelineMessage;
import org.zmonitor.message.StringMessage;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class EventBusMessageHandler_TEST {
	
	@Test
	public void handleSimpleMessage() throws Exception{
		EventBusMessageHandler handler = new EventBusMessageHandler();
		handler.addMessageEventListener(new EvtListener1());
		handler.addMessageEventListener(new EvtListener2());
		handler.handle(new StringMessage("This is a String!"), true);
		handler.handle(new NewTimelineMessage(), false);
	}

	
	public static class EvtListener1{
		public void onFxxk(String kk){
			System.out.println(">>> This is not a event Listening method!");
		}
		public void Fxxk(String kk){
			System.out.println(">>> nor this one~");
		}
		
		public void onAddTimeline(MessageEvent<NewTimelineMessage> evt){
			System.out.println(">>> onAddTimeline:: MessageEvent<NewTimelineMessage>");
		}
		public void onString(MessageEvent<StringMessage> evt){
			System.out.println(">>> onString:: MessageEvent<StringMessage>: "+evt.getMessage());
		}
		public void onXxxx(MessageEvent<Message> evt){
			//broad cast
		}
	}//end of class...
	
	public static class EvtListener2{
		public void onFxxk(String kk){
			System.out.println(">>> This is not a event Listening method!");
		}
		public void Fxxk(String kk){
			System.out.println(">>> nor this one~");
		}
		public void onAddTimeline(MessageEvent<NewTimelineMessage> evt){
			System.out.println(">>> EvtListener2:onAddTimeline:: MessageEvent<NewTimelineMessage>");
		}
		
		
		public void onString(MessageEvent<StringMessage> evt){
			System.out.println(">>> EvtListener2:onString:: MessageEvent<StringMessage>: "+evt.getMessage());
		}
	}//end of class...
}
