/**
 * 
 */
package org.zmonitor.util;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class StackTraceElementFinder {
	private final String[] ignoredPackages;
	
	/**
	 * 
	 */
	public StackTraceElementFinder(String... ignorPackages) {
		this.ignoredPackages = ignorPackages; 
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
	
	private static final StackTraceElement[] EMPTY = new StackTraceElement[0];
	/**
	 * 
	 * @param stackElemts
	 * @return
	 */
	public StackTraceElement[] truncate(StackTraceElement[] stackElemts){
		
		for(int i=0; i<stackElemts.length; i++){
			if(ignore(stackElemts[i])){
				continue;
			}
			StackTraceElement[] newArr = new StackTraceElement[stackElemts.length - i];
			System.arraycopy(stackElemts, i, newArr, 0, newArr.length);
			return newArr;
		}
		return EMPTY;
	}
	
	public static StackTraceElement[] truncate(int callerLevel){
		StackTraceElement[] stackElemts = Thread.currentThread().getStackTrace();
		callerLevel+=2;// for java.lang.Thread.getStackTrace
		//and org.zmonitor.util.StackTraceElementFinder.truncate
		if(stackElemts.length<=callerLevel){
		//this should never happened...
		throw new IndexOutOfBoundsException(" the callerLevel"+callerLevel
			+ " >= stacktrace depth: "+stackElemts.length);
		}
		StackTraceElement[] arr = new StackTraceElement[stackElemts.length-callerLevel];
		System.arraycopy(stackElemts, callerLevel, arr, 0, arr.length);
		return arr; 
	}
	
	
	private boolean ignore(StackTraceElement element){
		String clzName = element.getClassName();
		if(clzName==null)return true;

		for(String ignorePkg : ignoredPackages){
			if(clzName.startsWith(ignorePkg)){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args){
		 StackTraceElement[] arr = truncate(0);
		 for(StackTraceElement e : arr){
			 System.out.println("StackTraceElement: "+e);
		 }
	}
	
}
