/**
 * 
 */
package org.zmonitor.webtest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.zkoss.monitor.server.grizzly.MasterService;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.message.Message;
import org.zmonitor.message.MessageHandler;
import org.zmonitor.message.MonitorSequenceMessage;
import org.zmonitor.test.junit.MonitoredResult;
import org.zmonitor.web.Defaults;
import org.zmonitor.webtest.impl.SimpleMonitorSequenceStore;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public final class ZMonitorWebTestMaster extends ZMBeanBase{

	private int port = Defaults.WEB_TEST_MASTER_PORT;
	private String requestUuidParam = 
		Defaults.WEB_TEST_MASTER_REQ_UUID_PARAM;
	private String host = "localhost";
	private MonitorSequenceStore msStore;
	
	public ZMonitorWebTestMaster(){
		setId(ZMonitorWebTestMaster.class.getName());
	}
	
	public void setStore(MonitorSequenceStore msStore) {
		this.msStore = msStore;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}


	public String getRequestUuidParam() {
		return requestUuidParam;
	}
	public void setRequestUuidParam(String requestUuidParam) {
		this.requestUuidParam = requestUuidParam;
	}
	MasterService aMasterService;
	

	@Override
	protected void doStart() {
		if(aMasterService!=null){
			//TODO close the previous one
		}
		if(msStore==null){
			msStore = new SimpleMonitorSequenceStore();
		}
		
		aMasterService = 
				new MasterService(host, port);
		aMasterService.setMessageHandler(new MessageHandler() {
			public Message handle(Message request, boolean hasCallback)
					throws Exception {
				MonitorSequenceMessage msMesg = (MonitorSequenceMessage) request;
				List<MonitorSequence> mss = msMesg.getAll();
				msStore.addAll(mss);
				return null;
			}
		});
		try {
			aMasterService.init();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	@Override
	protected void doStop() {
		try {
			aMasterService.distroy();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static final String USER_AGENT = "Mozilla/5.0";
	/**
	 * 
	 * @param req
	 * @param timeout
	 * @return
	 * @throws UnableToReceiveResultException 
	 */
	public WebTestResponse awaitForMSTransmission(HttpUriRequest req, long timeout) 
			{
//		Thread thread = Thread.currentThread();// get caller thread.
		
		// add header
		req.setHeader("User-Agent", USER_AGENT);
		
		
		String uuid = UUID.randomUUID().toString();
		
//		req.getParams().setParameter(requestUuidParam, uuid);
		
		URIBuilder builder = new URIBuilder(req.getURI()).addParameter(requestUuidParam, uuid);
		try {
			((HttpRequestBase) req).setURI(builder.build());
		} catch (URISyntaxException e1) {
			throw new RuntimeException(e1);
		}
		
		//TODO
		//1. 
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(req);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		//TODO should I handle the response code?
		StatusLine sLine = response.getStatusLine();
		
		System.out.println("Response Code : " 
                + sLine.getStatusCode());
		long startMillis = System.currentTimeMillis(); 
		WebTestResponse wResp = new WebTestResponse();
		while(true){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				ZMLog.warn(e);
			}
			
			if(msStore.contains(uuid)){
				wResp.setMonitoredResult(
					new MonitoredResult(msStore.drainAll()));
				
				break;
			}
			if(System.currentTimeMillis() - startMillis > 3000){
				throw new UnableToReceiveResultException(sLine);
			}
		}
		return wResp;
	}

	

}

