/**JObjSStreamCommunicator.java
 * 2011/10/14
 * 
 */
package org.zmonitor.message.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Callback;
import org.zmonitor.message.Communicator;
import org.zmonitor.message.Message;
import org.zmonitor.message.MessageHandler;
import org.zmonitor.message.Parcel;
import org.zmonitor.message.Receiever;
import org.zmonitor.message.Transmitter;
import org.zmonitor.util.Streams;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JObjStreamCommunicator implements Communicator {
	
	private static final int TRANSMITTER_BUFFER_SIZE = 4 * 1024;
	protected static final int TIME_OUT_VALUE = 3000;
	
	private final String host;
	private final int port;	
	private InnerTransmitter transmitter;
	private InnerReceiver receiver;
	private String clientId;//tunnel info 
	private int threshold;
	private long waitMillis = -1;
	/**
	 * 
	 * @param zMgmt
	 */
	public JObjStreamCommunicator( String host, int port){
		this.host = host;
		this.port = port;
		transmitter = new InnerTransmitter();
		receiver = new InnerReceiver();
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.message.Communicator#getTransmitter()
	 */
	public Transmitter getTransmitter() {
		return transmitter;
	}

	/* (non-Javadoc)
	 * @see org.zmonitor.message.Communicator#getReceiever()
	 */
	public Receiever getReceiever() {
		return receiver;
	}
	
	
	private final AtomicInteger atomicInt = new AtomicInteger();
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private class InnerTransmitter implements Transmitter{
		private RegisteredMessageManager regManager = new RegisteredMessageManager();
		private AsyncGroupingPipe<Message> asyncGroupPipe;
		
		InnerTransmitter(){ 
			asyncGroupPipe = new AsyncGroupingPipe<Message>(threshold, waitMillis,
					new AsyncGroupingPipe.Executor<Message>() {
				private byte[] buffer = new byte[TRANSMITTER_BUFFER_SIZE];
				
				public void flush(List<Message> mesgs) throws Exception{
					// TODO: Marshalling timeline to timeline-service socket.
					 
					// 1. Open a socket to remote server.
					// 2. Create an ObjectOutputStream which decorates socket.outputStream.
					// 3. Flush the output
					// 4. Read the response from server, check if the operation success.
					// 5. close the socket, or find a way to reuse it.
					
					Socket socket = null;
					try {
						
						socket = new Socket();
						socket.bind(null);
						socket.connect(new InetSocketAddress(host ,port), TIME_OUT_VALUE);	
						System.out.println("opening Client socket at:"+socket.getLocalPort()+", transport "+mesgs.size()+" timelines");
						
						// 0. Initialize a parcel for metaInfo and Messages
						Parcel p = new Parcel();
						p.add(mesgs);
						p.setAgentId(clientId);
						byte[] data = Streams.serialize(p); 
						
//						flushData(data, new FileOutputStream("C:\\ZM_LOG\\zmonitor_dosend_" +
//								atomicInt.getAndIncrement()+".log"), buffer);
						
						OutputStream socketOutput = socket.getOutputStream();
						
						flushData(data, socketOutput, buffer);
						
						// read an object from the server
						try{
							ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
							Parcel response = (Parcel) ois.readObject();
							for(Message mesg : response){
								regManager.acknowledge(mesg, null);	
							}
						}catch(java.io.EOFException eof){
							eof.printStackTrace();
						}
						
					} finally{
						try {
							if(socket!=null)socket.close();
						} catch (IOException e) {
							ZMLog.warn(e, "falid to close socket properly, host:"+host+", port:"+port);
						}
					}
				}
				public boolean failover(Exception e, List<Message> workingList) {
					e.printStackTrace();
					for(Message regMesg: workingList){
						regManager.acknowledge(regMesg, e);	
					}
					return true;
				}
			});
		}
		
		public void send(Message message) {
			asyncGroupPipe.push(message);
			asyncGroupPipe.flush();
		}

		public void post(Message message, Callback callback) {
			if(callback==null){
				asyncGroupPipe.push(message);
			}else{
				asyncGroupPipe.push(regManager.register(message, callback));	
			}
			
		}
		
	}//end of class...
	
	private static void flushData(byte[] data, OutputStream out, byte[] buffer) throws IOException{
		ByteArrayInputStream ain = new ByteArrayInputStream(data);
		if(buffer==null)
			buffer = new byte[TRANSMITTER_BUFFER_SIZE];
		Streams.flush(ain, out, buffer);
	}
	/**
	 * 
	 * @return
	 */
	public static Receiever newReceiever(){
		return new InnerReceiver();
	}
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private static class InnerReceiver implements Receiever{
		private MessageHandler fMessageHandler;
		
		public void setHandler(MessageHandler handler) {
			fMessageHandler = handler;
		}

		public Message handle(Message req){
			Serializable respContent = null;
			try {
				respContent = 
					RegisteredMessageManager.invoke(req, fMessageHandler); 
					
			} catch (Exception e) {
				respContent = e;
			}
			Message respMesg = 
				RegisteredMessageManager.generateAR(req, respContent);
			
			return respMesg;
		}
	}
}//end of class...








