/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.ZMonitorManager;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.spi.Configurator;
import org.zmonitor.util.Arguments;
import org.zmonitor.util.PropertySetter;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class ConfiguratorBase extends ZMBeanBase implements Configurator{
	protected static final String REL_PUSH = "push"; 
	protected static final String REL_POP = "pop";
	
	protected final String confNodeName;
	protected final TrackerBase tracker;
	
	
	public ConfiguratorBase(String zmBeanId, String confNodeName, TrackerBase tracker){
		Arguments.checkNotEmpty(zmBeanId);
		Arguments.checkNotEmpty(confNodeName);
		Arguments.checkNotNull(tracker);

		this.confNodeName = confNodeName;
		this.tracker = tracker;
		this.setId(zmBeanId);
	}
	
	public void configure(ConfigContext monitorMgmt) {
		ZMonitorManager manager = monitorMgmt.getManager();
		
		configureDefault(manager);
		
		ConfigContext confCtx = monitorMgmt.toNode(confNodeName);
		if(confCtx.getNode()!=null){
			PropertySetter confSetter = new PropertySetter(this);
			confCtx.applyAttributes(confSetter);
			configureByContext(confCtx);	
		}
	}
	/**
	 * will be called in very beginning while configure.
	 * @param manager
	 */
	protected abstract void configureDefault(ZMonitorManager manager);
	/**
	 * will be called if there's a confNode.
	 * @param confCtx
	 */
	protected abstract void configureByContext(ConfigContext confCtx);
	
	
	
	
	protected void preparePushOp(ConfigContext pushOpCtx){
		String op = pushOpCtx.getContent();
		if(Strings.isEmpty(op))tracker.setPushOp(op);
	}
	
	protected void preparePopOp(ConfigContext popOpCtx) {
		String op = popOpCtx.getContent();
		if(Strings.isEmpty(op))tracker.setPopOp(op);
	}
	
	public TrackerBase getTracker(){
		return tracker;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isEatOperator(){
		return tracker.isEatOperator();
	}
	/**
	 * 
	 * @param eatOp
	 */
	public void setEatOperator(boolean eatOp){
		tracker.setEatOperator(eatOp);
	}
	
}
