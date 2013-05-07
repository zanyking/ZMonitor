/**
 * 
 */
package org.zmonitor.handler.text;

/**
 * 
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface Renderer<T extends RenderContext> {
	/*
	 * provide API for template definition & customization
	 */
	
	/*
	 * provide API to render a .
	 */
	/**
	 * 
	 * @param renderingCtx
	 */
	public void render (T renderingCtx);
}
