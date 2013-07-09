/**
 * 
 */
package org.zmonitor.webtest;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.junit.After;
import org.junit.Before;
import org.zmonitor.InitFailureException;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.config.ConfigSource;
import org.zmonitor.config.URLConfigSource;
import org.zmonitor.impl.ThreadLocalMonitorLifecycleManager;
import org.zmonitor.impl.ZMLog;
import org.zmonitor.impl.MSPipe.Mode;
import org.zmonitor.spi.MonitorLifecycle;
import org.zmonitor.test.junit.TestBaseUtils;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class WebTestBase {
	
	
	protected void initZMonitorManager() throws Exception{
		ZMonitorManager aZMonitorManager = new ZMonitorManager();
		
		String packagePath = this.getClass().getPackage().getName().replace('.', '/');
		URL url =  TestBaseUtils.findSetting(packagePath);
		if(url==null){
			throw new InitFailureException("cannot find Configuration:["+
					ConfigSource.ZMONITOR_XML+
					"] from every level of package: [" +packagePath+
					"]. Current application context is: "+this.getClass());
		}
		ZMLog.info("ZMonitor JUnit TestBase: load config from: [",url,"]");
		
		aZMonitorManager.performConfiguration(new URLConfigSource(url));
		Mode mode = aZMonitorManager.getMSPipe().getMode();
		if(Mode.SYNC != mode){
			throw new IllegalStateException(
				"the mode of MSPipe must be synchronized during test!  pipe mode:"+mode); 
		}
		
		ThreadLocalMonitorLifecycleManager lifecycleMgmt = 
			new ThreadLocalMonitorLifecycleManager();
		
		aZMonitorManager.setLifecycleManager(lifecycleMgmt);
		

		ZMonitorManager.init(aZMonitorManager); // this step makes the given
												// ZMonitorManager became
												// default in ClassLoader. 
		ZMLog.info(">> Ignit ZMonitor in: ",this.getClass().getCanonicalName());
	}
	@Before
	public void setUp() throws Exception {
//		beforeZmonitorManagerInit();
		if(!ZMonitorManager.isInitialized()){
			initZMonitorManager();
		}
//		afterZmonitorManagerInit();
		//TODO: see if there's any alternative to do this part...
		
		finishMonitorLifecycle();
	}
	@After
	public void dispose()throws Exception {
		ZMonitorManager.dispose();
	}
	
	private void finishMonitorLifecycle(){
		MonitorLifecycle lifecycle = ZMonitorManager.getInstance().getLifecycleManager().getLifecycle();
		if(lifecycle.isMonitorStarted()&&!lifecycle.isFinished())
			lifecycle.finish();//force flush current monitorSequence.
	}
	
	
	
	
	
	protected WebTestResponse doGet(String url, Map<String, Object> args){
		HttpGet get = new HttpGet(url);
		
		if(args!=null){
			HttpParams params = get.getParams();
			for(Map.Entry<String, Object> entry : args.entrySet()){
				params.setParameter(entry.getKey(), entry.getValue());
			}
		}
		
		return sendRequest(get);
	}
	
	protected WebTestResponse doPost(String url, Map<String, Object> args){
		HttpPost post = new HttpPost(url);
		
		
		if(args!=null){
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			for(Map.Entry<String, Object> entry : args.entrySet()){
				urlParameters.add(
					new BasicNameValuePair(entry.getKey(), toString(entry.getValue())));
			}
			try {
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		
		return sendRequest(post);
	}
	
	private static String toString(Object obj){
		if(obj==null)return "";
		return obj.toString();
	}

	private WebTestResponse sendRequest(HttpUriRequest req) {
		
		//1. Decorate URL
		//2. Forge a browser request and send request
		//3. Confirmed response
		//4. Wait monitored result from client side by given timout
		// (add current thread to ZMonitorWebMaster's waiting list)
		//5. ZMonitorWebTestMaster transmission.
		//6. get a result Ref;
		ZMonitorWebTestMaster master = 
			WebTestConfigurator.getInstance().getWebTestMaster();
		long timeout = 10000L;
		WebTestResponse resultRef  = master.awaitForMSTransmission(req, timeout);
		return resultRef;
	}
	
	
}
