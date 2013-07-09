/**
 * 
 */
package org.zmonitor.web.test;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.zmonitor.config.ConfigContext;
import org.zmonitor.message.impl.JObjStreamCommunicator;
import org.zmonitor.web.Defaults;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TestConfig {
	private String requestUuidParam = Defaults.WEB_TEST_MASTER_REQ_UUID_PARAM;
	private boolean activate = true;
	private String webTesterHost = "localhost";
	private int webTesterPort = Defaults.WEB_TEST_MASTER_PORT;
	private String runtimeId; 
	private long waitMillis = -1;
	
	public void init(ConfigContext testConfCtx) {
		if(!activate)return;
		
		if(runtimeId==null){
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			runtimeId = runtimeMXBean.getName();
		}
		TransmissionMonitorSequenceHandler handler = 
				new TransmissionMonitorSequenceHandler();
		JObjStreamCommunicator communicator = 
			new JObjStreamCommunicator(webTesterHost, webTesterPort); 
		communicator.setClientId(runtimeId);
		
		handler.setCommunicator(communicator);
		handler.init(testConfCtx.getManager());
		testConfCtx.getManager().addMonitorSequenceHandler(handler);
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

	public String getWebTesterHost() {
		return webTesterHost;
	}

	public void setWebTesterHost(String webTesterHost) {
		this.webTesterHost = webTesterHost;
	}

	public int getWebTesterPort() {
		return webTesterPort;
	}

	public void setWebTesterPort(int webTesterPort) {
		this.webTesterPort = webTesterPort;
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


}
