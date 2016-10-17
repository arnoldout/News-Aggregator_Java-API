package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import java.lang.reflect.Type;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mongodb.client.MongoCollection;

import main.java.types.Profile;
import main.java.types.TagViewPair;

public class ProfileService extends MongoService {
	private MongoCollection<Document> col;
	public ProfileService(MongoConnection db) {
		super();
		super.setDb(db.getDb());
		col = super.getCollection("profile");
	}
	public Profile getProfile(ObjectId id)
	{
		GsonWrapper gw = new GsonWrapper();
		Document d = (Document) col.find(eq("_id", id)).first();
		return gw.getGson().fromJson(d.toJson(), Profile.class);
	}
	public void incrementTag(String tagName, ObjectId oid)
	{	 
		Profile p = getProfile(oid);
		GsonWrapper gw = new GsonWrapper();
		Gson g = gw.getGson();
		MongoCollection<Document> keyPairs = super.getCollection("tagPairs");
		for(String id : p.getLikes())
		{
			Document tagPair = (Document) keyPairs.find(eq("_id", new ObjectId(id))).first();
			TagViewPair tvp = g.fromJson(tagPair.toJson(), TagViewPair.class);
			if(tvp.getTag().equals(tagName)){
				tvp.incrementViewCount();
				Document dd = tvp.makeDocument();
				keyPairs.replaceOne(eq("_id", tvp.get_id()), tvp.makeDocument());
				return;
			}
		}
		TagViewPair kvp = new TagViewPair(new ObjectId(), tagName, 1);
		keyPairs.insertOne(kvp.makeDocument());
		p.addLike(kvp.get_id());
		
		col.replaceOne(eq("_id", p.getId()), p.makeDocument());
	}
}
