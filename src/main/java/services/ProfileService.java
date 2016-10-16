package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import main.java.types.Profile;

public class ProfileService extends MongoService {
	private MongoCollection<Document> col;
	public ProfileService(MongoConnection db) {
		super();
		super.setDb(db.getDb());
		col = super.getCollection("profile");
	}
	public Profile getProfile(ObjectId id)
	{
		Document d = (Document) col.find(eq("_id", id)).first();
		Gson g = new Gson();
		return g.fromJson(d.toJson(), Profile.class);
	}
	
	public void addLikeCount(String usrName, String tagName)
	{
		Gson g = new Gson();
		Document d = (Document) col.find(eq("username", usrName)).first();
		Profile usr = g.fromJson(d.toJson(), Profile.class);
		usr.incrementTag(tagName);
		Document dd = usr.makeDocument();
		col.replaceOne(eq("_id", usr.getId()), dd);
	}
}
