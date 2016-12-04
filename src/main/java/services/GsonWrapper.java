package main.java.services;

import java.lang.reflect.Type;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

//adapted from http://stackoverflow.com/questions/13141245/load-id-mongodb-my-object-in-java
public class GsonWrapper {
	private JsonDeserializer<ObjectId> des = new JsonDeserializer<ObjectId>() {

        @Override
        public ObjectId deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new ObjectId(je.getAsJsonObject().get("$oid").getAsString());
        }

    };

    private Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, des).create();

	public Gson getGson() {
		return gson;
	}
}
