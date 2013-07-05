/**
 * 
 */
package org.zmonitor.test.web;

import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebTestBase {
	
	protected RequestResult doGet(String url){
		//TODO form a request Object 
		RequestObject reqObj = new RequestObject();
		RequestResult t = sendRequest(reqObj);
		return t;
	}
	
	protected RequestResult doPost(String url, Object... args){
		//TODO form a request Object 
		RequestObject reqObj = new RequestObject();
		RequestResult t = sendRequest(reqObj);
		return t;
	}

	private RequestResult sendRequest(RequestObject req) {
		
		//1. Decorate URL
		//2. Forge a browser request and send request
		//3. Confirmed response
		//4. Wait monitored result from client side by given timout
		// (add current thread to ZMonitorWebMaster's waiting list)
		//5. ZMonitorWebTestMaster transmission.
		//6. get a result Ref;
		ZMonitorWebTestMaster master = ZMonitorWebTestMaster.getInstance();
		long timeout = 10000L;
		RequestResult resultRef  = master.awaitForMSTransmission(req, timeout);
		return resultRef;
	}
	
	
}
