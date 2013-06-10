/**
 * 
 */
package org.zmonitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CallerStackTraceElementFinder {
	private final String[] ignorPackages;
	
	/**
	 * 
	 */
	public CallerStackTraceElementFinder(String... ignorPackages) {
		this.ignorPackages = ignorPackages; 
	}
	/**
	 * 
	 * @param throwable
	 * @return get the {@link StackTraceElement} through throwable.getStackTrace();
	 */
	public StackTraceElement find(Throwable throwable ){
		return find(throwable.getStackTrace());
	}
	/**
	 * get the {@link StackTraceElement} through Thread.currentThread().getStackTrace();  
	 * @return
	 */
	public StackTraceElement find(){
		return find(Thread.currentThread().getStackTrace() );
	}
	/**
	 * 
	 * @param callerLevel
	 * @return
	 */
	public StackTraceElement find(StackTraceElement[] stackElemts ){
		for(StackTraceElement stEle : stackElemts){
			if(ignore(stEle)){
				continue;
			}
			return stEle;
		}
		return null;
	}
	
	private boolean ignore(StackTraceElement element){
		String clzName = element.getClassName();
		if(clzName==null)return true;
		
		for(String ignorePkg : ignorPackages){
			if(clzName.startsWith(ignorePkg)){
				return true;
			}
		}
		return false;
	}
	
	
}
