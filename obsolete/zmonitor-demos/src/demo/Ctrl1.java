package demo;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class Ctrl1 extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PerformanceOverviewCtrl.class);
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug("initial");
	}
	
	private Service1 getService(){
		return new Service1();
	}
	
	public void onClick$act1(){
		
		logger.debug("before call action1");
		getService().action1();
		logger.debug("after call action1");
		logger.debug("before call action2");
		getService().action2();
		logger.debug("after call action2");
	}
	
	public void onClick$act2(){
		logger.debug("before call action3");
		getService().action3();
		logger.debug("after call action3");
	}

	
}
