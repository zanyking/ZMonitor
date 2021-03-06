/**ZKProfilingConfigurator.java
 * 2011/4/5
 * 
 */
package org.zmonitor.zk;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.config.WrongConfigurationException;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZKInterceptorCustom extends ZMBeanBase implements CustomConfigurable {
	
	public void configure(ConfigContext configCtx) {
		//DO nothing now.
	}
	private ZKInterceptorMPRenderer mpRenderer = new ZKInterceptorMPRenderer();
	public ZKInterceptorMPRenderer getMpRenderer() {
		return mpRenderer;
	}
	public void setMpRenderer(ZKInterceptorMPRenderer mpRenderer) {
		this.mpRenderer = mpRenderer;
	}
	
	
	public void setRendererClass(String className){
		if(ZKInterceptorMPRenderer.class.getName().equals(className))
			return;
		
		try {
			Object instance = Class.forName(className).newInstance();
			if(instance instanceof ZKInterceptorMPRenderer){
				setMpRenderer((ZKInterceptorMPRenderer) instance);
			}else{
				throw new WrongConfigurationException("the given className: " +className
						+" is not instanceof "+ZKInterceptorMPRenderer.class.getCanonicalName());
			}
		} catch (Exception e) {
			throw new WrongConfigurationException(e);
		}
	}
	
	
	private boolean renderURIIntercepter = true;
	public boolean isRenderURIIntercepter() {
		return renderURIIntercepter;
	}
	public void setRenderURIIntercepter(boolean renderURIIntercepter) {
		this.renderURIIntercepter = renderURIIntercepter;
	}
	
	private boolean renderExecutionInit = true;
	public boolean isRenderExecutionInit() {
		return renderExecutionInit;
	}
	public void setRenderExecutionInit(boolean renderExecutionInit) {
		this.renderExecutionInit = renderExecutionInit;
	}
	
	private boolean renderBeforeSendEvent = true;
	public boolean isRenderBeforeSendEvent() {
		return renderBeforeSendEvent;
	}
	public void setRenderBeforeSendEvent(boolean renderBeforeSendEvent) {
		this.renderBeforeSendEvent = renderBeforeSendEvent;
	}
	
	private boolean renderBeforePostEvent = true;
	public boolean isRenderBeforePostEvent() {
		return renderBeforePostEvent;
	}
	public void setRenderBeforePostEvent(boolean renderBeforePostEvent) {
		this.renderBeforePostEvent = renderBeforePostEvent;
	}
	
	
	private boolean renderProcessEvent = true;
	public boolean isRenderProcessEvent() {
		return renderProcessEvent;
	}
	public void setRenderProcessEvent(boolean renderProcessEvent) {
		this.renderProcessEvent = renderProcessEvent;
	}
	
	
	private boolean renderMonitorAsyncUpdate = true;
	public boolean isRenderMonitorAsyncUpdate() {
		return renderMonitorAsyncUpdate;
	}
	public void setRenderMonitorAsyncUpdate(boolean renderMonitorAsyncUpdate) {
		this.renderMonitorAsyncUpdate = renderMonitorAsyncUpdate;
	}
	
	
	
}
