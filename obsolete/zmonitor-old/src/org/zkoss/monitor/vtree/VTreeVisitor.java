/**ParseInterceptor.java
 * 2011/3/14
 * 
 */
package org.zkoss.monitor.vtree;


/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class VTreeVisitor {

	public abstract boolean begin(VersionNode node);
	
	public abstract boolean end(VersionNode node);
	
	public abstract boolean begin(VersionNodeChildren children);
	
	public abstract boolean end(VersionNodeChildren children);
	
}
