/**
 * 
 */
package demo.server;

import org.zkoss.monitor.Timeline;
import org.zkoss.monitor.handler.ToStringTimelineHandler;
import org.zkoss.monitor.message.NewTimelineMessage;
import org.zkoss.monitor.message.StringMessage;
import org.zkoss.monitor.server.EventBusMessageHandler;
import org.zkoss.monitor.server.MessageEvent;
import org.zkoss.monitor.server.grizzly.MasterService;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MasterService_TEST {

	
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PORT = 8799;
	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {

		MasterService aMasterService = new MasterService(SERVER_HOST, SERVER_PORT);
		try{
			EventBusMessageHandler handler = new EventBusMessageHandler();
			handler.addMessageEventListener(new MessageEvtListener());
			aMasterService.setMessageHandler(handler);
			aMasterService.init();
			
			System.out.println("press ENTER key to stop server....");
			System.in.read();
		}finally{
			aMasterService.distroy();
		}
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class MessageEvtListener{
		public void onAddTimeline(MessageEvent<NewTimelineMessage> evt){
			System.out.println(">>> onAddTimeline:: MessageEvent<NewTimelineMessage>");
			
			ToStringTimelineHandler handler = new ToStringTimelineHandler();
			for(Timeline tl : evt.getMessage().getAll()){
				handler.handle(tl);
			}
			
			if(evt.isHasResponse()){
				System.out.println("has response, write response back to server...");
			}
			
		}
		public void onString(MessageEvent<StringMessage> evt){
			System.out.println(">>> onString:: MessageEvent<StringMessage>: "+evt.getMessage());
		}
	}//end of class...

}
