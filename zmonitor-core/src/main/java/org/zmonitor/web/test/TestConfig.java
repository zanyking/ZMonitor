/**
 * 
 */
package org.zmonitor.web.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Callback;
import org.zmonitor.message.Message;
import org.zmonitor.message.Parcel;
import org.zmonitor.message.Transmitter;
import org.zmonitor.util.Streams;
import org.zmonitor.util.concurrent.AsyncGroupingPipe;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestConfig {
	private String requestUuidParam = "webTestUuid";
	private boolean activate = true;
	private String testerHost = "localhost";
	private int testerPort = 9755;
	private String runtimeId; 
	private long waitMillis = -1;
	
	public void init(ZMonitorManager zmMgmt) {
		if(!activate)return;
		
		if(runtimeId==null){
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			runtimeId = runtimeMXBean.getName();
		}
		TransmissionMonitorSequenceHandler handler = 
				new TransmissionMonitorSequenceHandler();
		handler.setTransmitter(new InnerTransmitter());
		zmMgmt.addMonitorSequenceHandler(handler);
	}
	
	public String getRequestUuidParam() {
		return requestUuidParam;
	}

	public void setRequestUuidParam(String uuidParam) {
		this.requestUuidParam = uuidParam;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}

	public String getTesterHost() {
		return testerHost;
	}

	public void setTesterHost(String host) {
		this.testerHost = host;
	}

	public int getTesterPort() {
		return testerPort;
	}

	public void setTesterPort(int testerPort) {
		this.testerPort = testerPort;
	}
	

	public long getWaitMillis() {
		return waitMillis;
	}

	public void setWaitMillis(long waitMillis) {
		this.waitMillis = waitMillis;
	}

	public String getRuntimeId() {
		return runtimeId;
	}
	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}



	private static final int TRANSMITTER_BUFFER_SIZE = 4 * 1024;
	protected static final int TIME_OUT_VALUE = 3000;
	/**
	 * 
	 * @author Ian YT Tsai(Zanyking)
	 *
	 */
	private class InnerTransmitter implements Transmitter{
		private RegisteredMessageManager regManager = new RegisteredMessageManager();
		private AsyncGroupingPipe<Message> asyncGroupPipe;
		
		InnerTransmitter(){ 
			asyncGroupPipe = new AsyncGroupingPipe<Message>(0, waitMillis,
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
						socket.connect(new InetSocketAddress(testerHost ,testerPort), TIME_OUT_VALUE);	
						System.out.println("opening Client socket at:"+socket.getLocalPort()+
								", transport "+mesgs.size()+" timelines");
						
						// 0. Initialize a parcel for metaInfo and Messages
						Parcel p = new Parcel();
						p.add(mesgs);
						p.setAgentId(runtimeId);
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
							ZMLog.warn(e, "falid to close socket properly, host:"+
									testerHost+", port:"+testerPort);
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
}
