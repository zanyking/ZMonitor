/**
 * 
 */
package org.zmonitor.selector.impl.zm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.zmonitor.MonitorPoint;
import org.zmonitor.marker.Marker;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPWrapper implements Entry<MonitorPoint>{
	private final MonitorPoint mp;
	private MCache mCache;
	public MPWrapper(MonitorPoint mp, MCache mCache) {
		this.mp = mp;
		if(mCache==null){
			mCache = new MCache(mp.getMonitorSequence());
		}
		this.mCache = mCache;
	}

	public EntryContainer<MonitorPoint> getEntryContainer() {
		return mCache.msw;
	}

	public Entry<MonitorPoint> getParent() {
		return mCache.toEntry(mp.getParent());
	}

	public int getIndex() {
		return mp.getIndex();
	}

	public int size() {
		return mp.size();
	}

	public boolean isEmpty() {
		return mp.isLeaf();
	}

	public Entry<MonitorPoint> getNextSibling() {
		return mCache.toEntry(mp.getNextSibling());
	}

	public Entry<MonitorPoint> getPreviousSibling() {
		return mCache.toEntry(mp.getPreviousSibling());
	}

	public Entry<MonitorPoint> getFirstChild() {
		return mCache.toEntry(mp.getFirstChild());
	}

	public MonitorPoint getValue() {
		return mp;
	}

	public String getType() {
		return mp.getMonitorMeta().getTrackerName();
	}

	public String getId() {
		return null;
	}
//	private static String toId(MonitorPoint mp){
//		mp.getMonitorMeta().get
//		return 
//	}

	public Set<String> getConceptualCssClasses() {
		Marker mk = mp.getMonitorMeta().getMarker();
		
		Set<String> ans = new HashSet<String>();
		ans.add(mk.getName());
		
		Iterator<Marker> it = mk.iterator();
		Marker reference;
		
		while (it.hasNext()) {
			reference =  it.next();
			ans.add(reference.getName());
		}
		return ans;
	}
}
