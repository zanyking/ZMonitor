/**
 * 
 */
package org.zmonitor.handler.json;

import java.lang.reflect.Type;

import org.zmonitor.MonitorPoint;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MonitorPointDeserializer implements JsonDeserializer<MonitorPoint>{

	public MonitorPoint deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		return null;
	}


}
