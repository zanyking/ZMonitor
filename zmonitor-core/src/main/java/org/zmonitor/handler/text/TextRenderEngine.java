/**
 * 
 */
package org.zmonitor.handler.text;

import java.io.Writer;

import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class TextRenderEngine {

	
	
	public TemplateDef getTemplateDef(){return null;}
	
	
	/**
	 * according to given TextRenderContext to look up a proper TextRenderer.
	 * @param txRendCtx
	 * @return
	 */
	public MonitorPointRenderer getTextRenderer(TextRenderContext txRendCtx){
		return null;
	}
	
	public void render(TextRenderContext txRendCtx){
		writeHeader(txRendCtx);
		dfsRender(txRendCtx, txRendCtx.getMonitorSequence().getRoot());
		writeFooter(txRendCtx);
	}


	private void dfsRender(TextRenderContext txRendCtx, MonitorPoint mp) {
		if(mp==null)return;
		//render self
		getTextRenderer(txRendCtx).render(txRendCtx, mp);
		//render children
	}

	protected void writeFooter(TextRenderContext txRendCtx) {
		Writer writer; 
		MonitorSequence ms;
	}


	protected void writeHeader(TextRenderContext txRendCtx) {
		Writer writer; 
		MonitorSequence ms;
	}
	
	
	
}
