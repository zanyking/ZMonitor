/**
 * 
 */
package org.zkoss.monitor.server.grizzly;

import java.io.Serializable;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.AbstractCodecFilter;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JObjectSSFilter extends AbstractCodecFilter<Buffer,Serializable>{

	/**
	 * 
	 */
	public JObjectSSFilter() {
		super(new JavaObjectDecoder(), new JavaObjectEncoder());
	}

}
