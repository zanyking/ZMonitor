/**
 * 
 */
package org.zmonitor.util.json;

import java.lang.reflect.Type;
import java.util.Iterator;

import org.zmonitor.MarkerFactory;
import org.zmonitor.marker.Marker;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class DefaultMarkerAdapter implements 
	JsonSerializer<Marker>, JsonDeserializer<Marker>{

	public Marker deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonArray mks = (JsonArray) json;

		boolean alreadyExist = false;
		Marker rootMk = null;
		Iterator<JsonElement> itor = mks.iterator();
		JsonPrimitive ele;
		while(itor.hasNext()){
			ele = (JsonPrimitive) itor.next();
			if(rootMk==null){
				alreadyExist = MarkerFactory.exist(ele.getAsString());
				rootMk = MarkerFactory.getMarker(ele.getAsString());
				continue;
			}
			if(alreadyExist)break;
			rootMk.add(MarkerFactory.getMarker(ele.getAsString()));
		}
		
		return rootMk;
	}

	public JsonElement serialize(Marker mk, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonArray mks = new JsonArray();
		serializeMarker(mk, mks);
		Iterator<Marker> itor = mk.iterator();
		while(itor.hasNext()){
			serializeMarker(itor.next(), mks);
		}
		return mks;
	}

	private static void serializeMarker(Marker mk, JsonArray mks){
		mks.add(new JsonPrimitive(mk.getName()));
	}
}
