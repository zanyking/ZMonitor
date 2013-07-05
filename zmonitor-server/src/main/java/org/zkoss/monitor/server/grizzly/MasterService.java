/**
 * 
 */
package org.zkoss.monitor.server.grizzly;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.zkoss.monitor.impl.JObjStreamCommunicator;
import org.zmonitor.message.Message;
import org.zmonitor.message.MessageHandler;
import org.zmonitor.message.Parcel;
import org.zmonitor.message.Receiever;


/**
 * 
 * A Master Service is a server-side implementation of ZMonitor based on Java Object Serialization Streaming Protocol.<br>
 * This implementation use Grizzly as it's core engine to leverage the power of Java NIO.
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MasterService {
	
	private SocketAddress address;
	private FilterChainBuilder fcBuilder;
	private MessageHandler messageHandler;
	private TCPNIOTransport transport;
	
	/**
	 */
	public MasterService(SocketAddress address){
		this.address = address;
	}
	/**
	 * 
	 * @param host
	 * @param port
	 */
	public MasterService(String host, int port){
		this.address = new InetSocketAddress(host, port);
	}
	/**
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException{
		if(messageHandler==null)throw new IllegalStateException(
				"You forgot to assign a "+MessageHandler.class.getSimpleName());
		
		fcBuilder = FilterChainBuilder.stateless()
		.add(new TransportFilter())
		.add(new JObjectSSFilter())
		.add(new ParcelFilter(messageHandler));
		
		if(transport!=null)
			throw new IllegalStateException();
		
		transport = TCPNIOTransportBuilder.newInstance().build();
		transport.setProcessor(fcBuilder.build());

		transport.bind(address);
		transport.start();
	}
	/**
	 * override this method to provide your own protocol of this MasterService instance.
	 * @param filters
	 */
	protected void initFilters(Filter... filters){
		
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void distroy() throws IOException{
		if(transport==null)return;
		try{
			transport.stop();
		}finally{
			transport = null;	
		}
	}
	/**
	 * 
	 * @return
	 */
	public MessageHandler getMessageHandler() {
		return messageHandler;
	}
	/**
	 * 
	 * @param messageHandler
	 */
	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	public static class ParcelFilter extends BaseFilter{
		final Receiever receiever = JObjStreamCommunicator.newReceiever();
		/**
		 * 
		 * @param handler
		 */
		public ParcelFilter(MessageHandler handler){
			receiever.setHandler(handler);
		}
		/*
		 * (non-Javadoc)
		 * @see org.glassfish.grizzly.filterchain.BaseFilter#handleRead(org.glassfish.grizzly.filterchain.FilterChainContext)
		 */
		public NextAction handleRead(FilterChainContext ctx) throws IOException {
			Parcel  messages = ctx.getMessage();
			
			Parcel respParcel = new Parcel();
			for(Message req : messages){
//				System.out.println("Message req: "+req);
				Message respMesg = receiever.handle(req);
				if(respMesg!=null)
					respParcel.add(respMesg);
			}
			ctx.write(respParcel);
			return ctx.getStopAction();
		}
		
	}//end of class...
}
