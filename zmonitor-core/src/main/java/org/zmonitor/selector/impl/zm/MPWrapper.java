/**
 * 
 */
package org.zmonitor.selector.impl.zm;

import java.util.Set;

import org.zmonitor.MonitorPoint;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;
import org.zmonitor.util.Strings;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MPWrapper implements Entry<MonitorPoint>{
	private final MonitorPoint mp;
	private MCache mCache;
	public MPWrapper(MonitorPoint mp, MCache mCache) {
		this.mp = mp;
		if(mp==null)throw new IllegalArgumentException("mp cannot be null!");
		if(mCache==null){
			mCache = new MCache(mp.getMonitorSequence());
		}
		this.mCache = mCache;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		Strings.append(sb, "MPWrapper ID:",getId(),
				", CLZ:",getConceptualCssClasses(),
				", MESG:",mp.getMessage());
		
		return sb.toString();
	}
	public EntryContainer<MonitorPoint> getEntryContainer() {
		return mCache.msw;
	}

	public Entry<MonitorPoint> getParent() {
		MonitorPoint temp = mp.getParent();
		if(temp==null)return null;
		return mCache.toEntry(temp);
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
		MonitorPoint temp = mp.getNextSibling();
		if(temp==null)return null;
		return mCache.toEntry(temp);
	}

	public Entry<MonitorPoint> getPreviousSibling() {
		MonitorPoint temp = mp.getPreviousSibling();
		if(temp==null)return null;
		return mCache.toEntry(temp);
	}

	public Entry<MonitorPoint> getFirstChild() {
		MonitorPoint temp = mp.getFirstChild();
		if(temp==null)return null;
		return mCache.toEntry(temp);
	}

	public MonitorPoint getValue() {
		return mp;
	}

	public String getId() {
		return mCache.msw.retrieveId(mp);
	}
	public String getType() {
		return mCache.msw.retrieveType(mp);
	}
	public Set<String> getConceptualCssClasses() {
		return mCache.msw.retrieveConceptualCssClasses(mp);
	}

	public int getSiblingSize() {
		MonitorPoint parent = mp.getParent();
		return (parent==null)? 0 : parent.size();
	}
}
