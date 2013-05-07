/**
 * 
 */
package org.zmonitor.handler.text;
import org.zmonitor.spi.*;

/**
 * TextRenderer is designed for text rendering infra to render 
 * ZmonitorManager's MessageFormatterFactory,  
 *  
 * @author Ian YT Tsai(Zanyking)
 *
 */
public interface MonitorPointRenderer {
	/*
	 * provide API for template definition & customization
	 */
	
	/*
	 * provide API to render a .
	 */
	/**
	 * 
	 * @param txRendCtx
	 */
	public void render(TextRenderContext txRendCtx);
}
