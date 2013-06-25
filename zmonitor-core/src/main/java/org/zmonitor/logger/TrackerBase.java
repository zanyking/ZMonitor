/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.TrackingContext;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class TrackerBase {

	public static final TrackerBase NULL_TRACKER = new TrackerBase() {
		public void tracking(TrackingContext tCtx) {
			//DO nothing...
		}
	};
	protected String pushOp = ">>";
	protected String popOp = "<<";
	protected boolean eatOperator = true;
	
	public String getPushOp() {
		return pushOp;
	}
	public void setPushOp(String pushOp) {
		this.pushOp = pushOp;
	}
	public String getPopOp() {
		return popOp;
	}
	public void setPopOp(String popOp) {
		this.popOp = popOp;
	}
	public boolean isEatOperator() {
		return eatOperator;
	}
	public void setEatOperator(boolean eatOperator) {
		this.eatOperator = eatOperator;
	}
	/**
	 * 
	 * @param tCtx
	 */
	public abstract void tracking(TrackingContext tCtx) ;
	
}
