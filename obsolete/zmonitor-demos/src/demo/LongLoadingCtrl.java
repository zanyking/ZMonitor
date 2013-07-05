package demo;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class LongLoadingCtrl extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Thread.sleep(1333);
		Events.postEvent("onNothing",comp,null);
	}
	
	public void onCreate() throws Exception{
		System.out.println(">>>onCreate");
		Thread.sleep(1333);
	}
	
	public void onNothing() throws Exception{
		System.out.println(">>>onNothing");
		Thread.sleep(1333);
	}
	
}
