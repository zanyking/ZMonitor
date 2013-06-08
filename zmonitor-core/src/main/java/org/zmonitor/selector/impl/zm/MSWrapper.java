/**
 * 
 */
package org.zmonitor.selector.impl.zm;

import java.util.Set;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.ZMonitorManager;
import org.zmonitor.selector.Entry;
import org.zmonitor.selector.EntryContainer;
import org.zmonitor.selector.SelectorAdaptor;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MSWrapper implements EntryContainer<MonitorPoint>{
	private final MonitorSequence ms;
	private MCache mCache;
	private SelectorAdaptor adaptor; 
	/**
	 * @param ms
	 */
	public MSWrapper(MonitorSequence ms){
		this(ms, null);
		adaptor = ZMonitorManager.getInstance().getSelectorAdaptor();
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
	public MonitorSequence getValue() {
		return ms;
	}
	
	public String retrieveId(MonitorPoint mp) {
		return adaptor.retrieveId(mp);
	}
	
	public String retrieveType(MonitorPoint mp) {
		return  adaptor.retrieveType(mp);
	}
	
	public Set<String> retrieveConceptualCssClasses(MonitorPoint mp) {
		return adaptor.retrieveConceptualCssClasses(mp);
	}
	public Object resolveVariable(String varName, Entry<MonitorPoint> e) {
		return adaptor.resolveVariable(varName, e.getValue());
	}
	

}
