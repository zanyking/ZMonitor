/**
 * 
 */
package org.zmonitor.logger;

import org.zmonitor.TrackingContext;
import org.zmonitor.marker.Marker;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class TrackerBase {

	public static final TrackerBase NULL_TRACKER = new TrackerBase() {
		public void doTrack(TrackingContext tCtx) {
			//DO nothing...
		}
	};
	protected String pushOp = ">>";
	protected String popOp = "<<";
	protected boolean eatOperator = true;
	
	
	protected Marker pushMarcker;
	protected Marker popMarcker;
	protected Marker trackingMarcker;
	
	
	
	public Marker getPushMarcker() {
		return pushMarcker;
	}
	public void setPushMarcker(Marker pushMarcker) {
		this.pushMarcker = pushMarcker;
	}
	public Marker getPopMarcker() {
		return popMarcker;
	}
	public void setPopMarcker(Marker popMarcker) {
		this.popMarcker = popMarcker;
	}
	public Marker getTrackingMarcker() {
		return trackingMarcker;
	}
	public void setTrackingMarcker(Marker recordMarcker) {
		this.trackingMarcker = recordMarcker;
	}
	
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
	public abstract void doTrack(TrackingContext tCtx) ;
	
}
