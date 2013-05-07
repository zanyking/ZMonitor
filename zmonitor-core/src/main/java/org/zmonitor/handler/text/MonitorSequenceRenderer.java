/**
 * 
 */
package org.zmonitor.handler.text;

import org.zmonitor.MonitorPoint;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public abstract class MonitorSequenceRenderer<T extends RenderContext> implements Renderer<T> {

	private Renderer<T> headerRenderer;
	private Renderer<T> footerRenderer;
	
	
	/**
	 * 
	 */
	public MonitorSequenceRenderer(){}
	/**
	 * @param headerRenderer
	 * @param footerRenderer
	 */
	public MonitorSequenceRenderer(Renderer<T> headerRenderer,
			Renderer<T> footerRenderer) {
		super();
		this.headerRenderer = headerRenderer;
		this.footerRenderer = footerRenderer;
	}

	/**
	 * according to the given TrackingContext to look up a proper mpRenderer.
	 * @param trackingContext
	 * @return
	 */
	protected abstract Renderer<T> getMPRenderer(MonitorPoint trackingContext);

	
	public void render(T renderCtx){
		headerRenderer.render(renderCtx);
		
		while(renderCtx.hasNext()){
			getMPRenderer(renderCtx.getCurrent()).render(
				(T) renderCtx.next());
		}
		footerRenderer.render(renderCtx);
	}
	
	
	public Renderer<T> getHeaderRenderer() {
		return headerRenderer;
	}
	public void setHeaderRenderer(Renderer<T> headerRenderer) {
		this.headerRenderer = headerRenderer;
	}

	public Renderer<T> getFooterRenderer() {
		return footerRenderer;
	}
	public void setFooterRenderer(Renderer<T> footerRenderer) {
		this.footerRenderer = footerRenderer;
	}

	



	
}
