/**
 * 
 */
package org.zmonitor.webtest;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.web.Defaults;
import org.zmonitor.webtest.impl.SimpleMonitorSequenceStore;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WebTestConfigurator extends ZMBeanBase implements Configurator {
	private static final String REL_WEB_TEST_CONF = "webtest-conf";
	private static final String REL_MS_STORE = "monitor-sequence-store";
	private static final String REL_WEB_TEST_MASTER = "web-test-master";
	
	public WebTestConfigurator() {
		this.setId(REL_WEB_TEST_CONF);
	}

	private ZMonitorWebTestMaster webTestMaster = 
			new ZMonitorWebTestMaster();
	
	
	public void configure(ConfigContext configCtx) {
		ConfigContext webTestConf = configCtx.toNode(REL_WEB_TEST_CONF);
		if(webTestConf.getNode()!=null){
			
			//init Web Test Master
			ConfigContext webTestMasterCtx = webTestConf.toNode(REL_WEB_TEST_MASTER);
			if(webTestMasterCtx.getNode()!=null){
				webTestMasterCtx.applyAttributes(
					new PropertySetter(webTestMaster));
			}
			
			//init MS Store
			ConfigContext msSoreCtx = webTestConf.toNode(REL_MS_STORE);
			if(msSoreCtx.getNode()!=null){
				MonitorSequenceStore msStore = msSoreCtx.newBean(
					SimpleMonitorSequenceStore.class, false);
				msSoreCtx.applyAttributes(
					new PropertySetter(msStore), "class");
				webTestMaster.setStore(msStore);
			}
			
			
		}
		configCtx.getManager().accept(webTestMaster);
		//use default configuration...
	}

	
	protected void doStop() {
	}

	

	public ZMonitorWebTestMaster getWebTestMaster(){ 
		return webTestMaster;
	}
	public static WebTestConfigurator getInstance() {
		return ZMonitorManager.getInstance().getBeanById(REL_WEB_TEST_CONF);
	}
}
