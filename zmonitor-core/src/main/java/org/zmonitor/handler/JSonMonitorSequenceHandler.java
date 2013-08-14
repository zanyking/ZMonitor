/**
 * 
 */
package org.zmonitor.handler;

import org.zmonitor.CustomConfigurable;
import org.zmonitor.MonitorPoint;
import org.zmonitor.MonitorSequence;
import org.zmonitor.bean.ZMBeanBase;
import org.zmonitor.config.ConfigContext;
import org.zmonitor.marker.Marker;
import org.zmonitor.spi.MonitorSequenceHandler;
import org.zmonitor.util.json.DefaultMarkerAdapter;
import org.zmonitor.util.json.DefaultMonitorPointAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class JSonMonitorSequenceHandler extends ZMBeanBase 
implements MonitorSequenceHandler, CustomConfigurable{

	public void configure(ConfigContext configCtx) {
		
	}

	public void handle(MonitorSequence mSequence) {
		GsonBuilder gBuilder = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(MonitorPoint.class, new DefaultMonitorPointAdapter())
				.registerTypeAdapter(Marker.class, new DefaultMarkerAdapter());
		Gson gson = gBuilder.create();
		String jsonOutput = gson.toJson(mSequence);
		System.out.println(jsonOutput);
		//TODO: target channel
	}


}
