/**
 * 
 */
package org.zmonitor.logger.logback;

import static org.zmonitor.logger.logback.Markers.MK_LOGBACK;
import static org.zmonitor.logger.logback.Markers.TRACKER_NAME_LOGBACK;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.impl.DefaultMPVariableResolver;
import org.zmonitor.impl.DefaultSelectorAdaptation;
import org.zmonitor.logger.AppenderConfiguratorBase;
import org.zmonitor.logger.MessageTupleTracker;
import org.zmonitor.marker.Marker;
/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class LogbackConfigurator extends AppenderConfiguratorBase{
	private static final String REL_LOGBACK_CONF = "logback-conf";
	
	
	public LogbackConfigurator() {
		super(REL_LOGBACK_CONF, 
				REL_LOGBACK_CONF, 
				new MessageTupleTracker(),
				"");
		tracker.setPushMarcker(Markers.MK_PUSH_LOGBACK);
		tracker.setPopMarcker(Markers.MK_END_LOGBACK);
		tracker.setTrackingMarcker(Markers.MK_RECORD_LOGBACK);
	}
	
	static LogbackConfigurator getInstance(){
		return ZMonitorManager.getInstance().getBeanIfAny(LogbackConfigurator.class);
	}

	protected void configureDefault(ZMonitorManager manager) {
		manager.getSelectorAdaptor().addSupport(Markers.TRACKER_NAME_LOGBACK, 
				new DefaultSelectorAdaptation() {
			public String retrieveType(MonitorPoint mp) {
				MonitorMeta meta = mp.getMonitorMeta();
				String trackerName = meta.getTrackerName();
				Marker marker = meta.getMarker();
				if(TRACKER_NAME_LOGBACK.equals(trackerName)){
					if(marker==null){
						return MK_LOGBACK.getName();
					}
					else{
						return marker.getName();	
					}
				}
				throw new IllegalArgumentException(" unsupported tracker: "+trackerName);
			}
			public Object resolveAttribute(String varName, MonitorPoint mp) {
				return new DefaultMPVariableResolver(mp).resolveVariable(varName);
			}
		});
	}



}
