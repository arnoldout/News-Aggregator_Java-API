package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import main.java.types.Profile;

public class ProfileService extends MongoService {
	
	public ProfileService(MongoConnection db) {
		super();
		super.setDb(db.getDb());
	}
	public Profile getProfile(ObjectId id)
	{
		MongoCollection<Document> col = super.getCollection("profile");
		Document d = (Document) col.find(eq("_id", id));
		Gson g = new Gson();
		return g.fromJson(d.toJson(), Profile.class);
	}
	public void addLikeCount(String usrName, String tagName)
	{
		Gson g = new Gson();
		MongoCollection<Document> col = super.getCollection("profile");
		Document d = (Document) col.find(eq("username", usrName));
		Profile usr = g.fromJson(d.toJson(), Profile.class);
		usr.incrementTag(tagName);
		col.replaceOne(eq("username", usrName), usr.makeDocument());
	}
}
