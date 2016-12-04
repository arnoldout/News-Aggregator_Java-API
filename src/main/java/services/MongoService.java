package main.java.services;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

//Mongo collection management
public abstract class MongoService {

	private MongoDatabase db;
	
	public MongoCollection<Document> getCollection(String coll)
	{
		if(!collectionExists(coll, getDb()))
		{
			getDb().createCollection(coll);
		}
		return getDb().getCollection(coll);
	}
	public boolean collectionExists(final String collectionName, MongoDatabase db) {
	    MongoIterable<String> collectionNames = db.listCollectionNames();
	    for (final String name : collectionNames) {
	        if (name.equalsIgnoreCase(collectionName)) {
	            return true;
	        }
	    }
	    return false;
	}
	public void setDb(MongoDatabase db) {
		this.db = db;
	}

	public MongoDatabase getDb() {
		return db;
	}
	

}