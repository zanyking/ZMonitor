/**
 * 
 */
package org.zmonitor.webtest;

import java.util.Iterator;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class EhCacheMonitorSequenceStore implements MonitorSequenceStore{
	
	private static final String CACHE_NAME = "ms";
	private final CacheManager  cacheManager = CacheManager.getInstance();
	private String cacheName = CACHE_NAME;
	
	public EhCacheMonitorSequenceStore(){}
	
	private Ehcache getCache() {
		return  cacheManager.getCache(cacheName);
	}
	
	private Element get(Object key){
		return getCache().get(key);
	}
	
	
	public Iterator<MonitorSequence> iterator(){
		return new Iterator<MonitorSequence>(){
			private final Iterator<String> keyItor = 
					getCache().getKeys().iterator();
			
			public boolean hasNext() {
				return keyItor.hasNext();
			}

			public MonitorSequence next() {
				String key = keyItor.next();
				return find(key);
			}

			public void remove() {
				throw new UnsupportedOperationException("immutable iterator");
			}
			
		};
	}
	
	public MonitorSequence find(String key){
		Element ele = get(key);
		return ele==null? 
				null : 
				(MonitorSequence) ele.getObjectValue();
	}
	
}
