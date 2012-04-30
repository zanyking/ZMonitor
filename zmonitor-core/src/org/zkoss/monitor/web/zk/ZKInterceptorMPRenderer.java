/**ZKInterceptorMPRenderer.java
 * 2011/4/6
 * 
 */
package org.zkoss.monitor.web.zk;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.spi.Name;
import org.zkoss.monitor.util.Strings;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Include;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class ZKInterceptorMPRenderer {
	
	public interface NAME{
		String URI = "ZUL_REQ";
		String SENT = "SENT";
		String POST = "POST";
		String EVENT_S = "EVENT_S";
		String EVENT_E = "EVENT_E";
		String EXECUTION_S = "EXECUTION_S";
		String EXECUTION_E = "EXECUTION_E";
		String ASYNC_UPDATE = "ASYNC_UPDATE";
	}
	
	public RenderResult getURIInterceptorResult(String uri){
		return new RenderResult(
				new StringName(NAME.URI, uri), 
				"ZUL Request: uri="+uri);
	}
	public RenderResult getExecutionInitResult(Execution exec, Execution parent, String uri){
		return new RenderResult(
			new StringName(NAME.EXECUTION_S, uri), 
			"Start a new Execution");
	}
	
	public RenderResult getExecutionCleanupResult(Execution exec, Execution parent){
		return new RenderResult(
			new StringName(NAME.EXECUTION_E), 
			"Cleanup a Execution");
	}
	
	public RenderResult getBeforeSendEventResult(Event event){
		return new RenderResult(genName(NAME.SENT, event), "before send");
	}
	public RenderResult getBeforePostEventResult(Event event){
		return new RenderResult(genName(NAME.POST, event), "before post");
	}
	public RenderResult getBeforeProcessEventResult(Event event){
		Component comp = event.getTarget();
		StringBuffer mesg = new StringBuffer("start process event ");
		if(comp!=null && comp instanceof Include){
			Include inc = (Include) comp;
			mesg.append(": ").append(inc.getSrc());
		}
		return new RenderResult(genName(NAME.EVENT_S, event), mesg.toString());
	}
	public RenderResult getAfterProcessEventResult(Event event){
		return new RenderResult(genName(NAME.EVENT_E, event), "end process event ");
	}
	
	private static Name genName(String prefix, Event event){
		Component comp = event.getTarget();
		if(comp==null){// this must be a dummy event or something!
			return genNameForDummyEvent(prefix, event);
		}
		
		String suffix = Strings.append(event.getName(),":<",
				comp.getClass().getSimpleName(),
				" ",comp.getId(),
				">");
		
//		String pid = null;
//		Page page = comp.getPage();
//		if(page!=null){
//			pid = page.getId();
//			if(ComponentsCtrl.isAutoUuid(pid)){
//				pid = null;
//			}
//		}
//		if(Strings.isEmpty(pid)){
//			
//		}else{
//			suffix = Strings.append(pid,":",event.getName(),"<",
//					comp.getClass().getSimpleName(),
//					" ",comp.getId(),
//					">");
//		}
		return new StringName(prefix, suffix);
	}
	
	/*
	 * if the Event is not really a Component UI event (which means there's no target component). 
	 * then it should be handled by this one.
	 */
	private static Name genNameForDummyEvent(String prefix, Event event){
		Execution exec = Executions.getCurrent();
		HttpServletRequest req  = (HttpServletRequest) exec.getNativeRequest();
		String suffix = event.getName() +" : "+ req.getRequestURL();
		return new StringName(prefix, suffix);
	}
	
	public RenderResult getMonitorAsyncUpdateResult(List<AuRequest> requests, Desktop desktop){
		ZkAuName zauName = new ZkAuName("ASYNC_UPDATE");
		for(AuRequest req: requests){
			zauName.add(req.getCommand(), req.getComponent());
		}
		return new RenderResult(zauName, "BEFORE Event Handling "+desktop);
	}
	
}
