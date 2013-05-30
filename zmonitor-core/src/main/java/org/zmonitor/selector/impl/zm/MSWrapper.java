/**
 * 
 */
package org.zmonitor.selector.impl.zm;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MSWrapper implements EntryContainer{
	private final MonitorSequence ms;
	private MCache mCache;
	/**
	 * @param ms
	 */
	public MSWrapper(MonitorSequence ms){
		this(ms, null);
	}
	/**
	 * @param ms
	 * @param mCache
	 */
	public MSWrapper(MonitorSequence ms, MCache mCache) {
		this.ms = ms;
		if(mCache==null){
			this.mCache = new MCache(this);
		}else{
			this.mCache = mCache;	
		}
	}

	public Entry<MonitorPoint> getFirstRoot() {
		return mCache.toEntry(ms.getRoot());
	}

	public int size() {
		return 1;
	}
	public MonitorSequence getObject() {
		return ms;
	}
}
