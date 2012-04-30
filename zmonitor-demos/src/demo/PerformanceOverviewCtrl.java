/**PerformanceOverviewCtrl.java
 * 2011/3/22
 * 
 */
package demo;

import org.apache.log4j.Logger;
import org.zkoss.monitor.TimelineHandlerRepository;
import org.zkoss.monitor.ZMonitor;
import org.zkoss.monitor.ZMonitorManager;
import org.zkoss.monitor.handler.VersionTreeTimelineHandler;
import org.zkoss.monitor.impl.StringName;
import org.zkoss.monitor.vtree.ToStringVTreeVisitor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Html;

/**
 * @author Ian YT Tsai
 *
 */
public class PerformanceOverviewCtrl extends GenericForwardComposer {

	private static final Logger logger = Logger.getLogger(PerformanceOverviewCtrl.class);
	
	private Html html;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug("log4j debug testing!!!");
		ZMonitor.push(new StringName("IN_JAVA","doA()"), ">>> doA()", false);
		
		try{
			try {
				Thread.sleep(99);
			} catch (InterruptedException e) {e.printStackTrace();
			}
			
			doA();	
		}finally{
			ZMonitor.pop(false);
		}
		PureJavaDemo.main(null);
		logger.debug("after PureJavaDemo");
		ToStringVTreeVisitor toStringVisitor = new ToStringVTreeVisitor("  ");
		TimelineHandlerRepository repository = ZMonitorManager.getInstance().getTimelineHandlerRepository();
		VersionTreeTimelineHandler vtree = (VersionTreeTimelineHandler) repository.get("vtree-WEB");
		if(vtree!=null){
			vtree.getVersiontree().accept(toStringVisitor);
			String content = org.zkoss.xml.XMLs.escapeXML(toStringVisitor.toString());
			html.setContent("<pre>"+content+"</pre>");
		}
	}
	
	
	private void doA(){
		
		try {
			ZMonitor.record( ">>> begin sleep", true);
			Thread.sleep(1234);
			ZMonitor.record( "<<< after Sleep", true);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ZMonitor.record( "error happened!!!"+e.getMessage(), false);
		}
	}
	
}
