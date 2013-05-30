/**
 * 
 */
package org.zmonitor.selector.impl.zm;

import java.util.HashMap;
import java.util.Map;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.Entry;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MCache {
	final MSWrapper msw;
	final Map<MonitorPoint, MPWrapper> mps =
			new HashMap<MonitorPoint, MPWrapper>();
	
	public MCache(MSWrapper msw) {
		this.msw = msw;
	}
	public MCache(MonitorSequence monitorSequence) {
		msw = new MSWrapper(monitorSequence);
	}
	/**
	 * 
	 * @param mp
	 * @return
	 */
	public Entry<MonitorPoint> toEntry(MonitorPoint mp){
		MPWrapper ans = mps.get(mp);
		if(ans==null){
			mps.put(mp, new MPWrapper(mp, this));
		}
		return ans;
	}
}
