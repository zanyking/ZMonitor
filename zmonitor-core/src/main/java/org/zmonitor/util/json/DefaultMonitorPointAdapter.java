/**
 * 
 */
package org.zmonitor.util.json;


import java.lang.reflect.Type;

import org.zmonitor.MonitorMeta;
import org.zmonitor.MonitorPoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultMonitorPointAdapter  implements JsonSerializer<MonitorPoint>, JsonDeserializer<MonitorPoint>{

	public MonitorPoint deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject jObj = (JsonObject) json;
		JsonPrimitive jIdx = (JsonPrimitive) jObj.get("index");
		JsonPrimitive jCMillis = (JsonPrimitive) jObj.get("createMillis");
		JsonArray jKids = (JsonArray) jObj.get("children");
		JsonObject JsonObject = (JsonObject) jObj.get("MonitorMeta");
		//TODO not done yet...
		jObj.get("message");
		return null;
	}

	
	public JsonElement serialize(MonitorPoint mp, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject jObj = new JsonObject();
//		context.serialize(src);
		jObj.add("index", new JsonPrimitive(mp.getIndex()));
		
		jObj.add("createMillis", new JsonPrimitive(mp.getCreateMillis()));
		
		System.out.println("create millis: "+jObj.get("createMillis"));
		serializeMessage(mp.getMessage(), jObj, context);
		serializeMonitorMeta(mp.getMonitorMeta(), jObj, context);
		serializeChildren(mp.getFirstChild(), jObj, context);
		return jObj;
	}
	
	private static void serializeChildren(
			MonitorPoint firstChild, 
			JsonObject jObj, 
			JsonSerializationContext context) {
		if(firstChild==null)return;
		JsonArray kids = new JsonArray();
		MonitorPoint temp = firstChild;
		while(temp!=null){
			kids.add(context.serialize(temp));
			temp = temp.getNextSibling();
		}
		jObj.add("children", kids);
	}

	private static void serializeMessage(Object mObj, JsonObject jObj,
			JsonSerializationContext context){
		if(mObj!=null){
			if(mObj instanceof String){
				jObj.add("message", new JsonPrimitive((String)mObj));	
			} else{
				jObj.add("message", context.serialize(mObj));	
			}	
		}
	}
	
	private static void serializeMonitorMeta(MonitorMeta mm, 
			JsonObject jObj,
			JsonSerializationContext context) {
		jObj.add("MonitorMeta", context.serialize(mm));
	}

}
