/**
 * 
 */
package org.zmonitor.selector.impl.temp;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MatchCtxCtrl {
	/**
	 * 
	 * @param selectorIndex
	 * @param position
	 */
	void setQualified(int selectorIndex, int position);
	/**
	 * 
	 * @param selectorIndex
	 * @param position
	 * @param qualified
	 */
	void setQualified(int selectorIndex, int position, boolean qualified) ;
	/**
	 * 
	 * @return
	 */
	boolean[][] getQualified();
	/**
	 * 
	 */
	void moveToNextSibling();
}
