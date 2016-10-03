package com.ie.sw.services;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class ProfileService {
	MongoDatabase db;

	public MongoDatabase getDb() {
		return db;
	}
	public MongoCollection<Document> getCollection(String coll)
	{
		if(!collectionExists(coll, db))
		{
			db.createCollection(coll);
		}
		return db.getCollection(coll);
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
	public ProfileService(MongoDatabase db) {
		super();
		this.db = db;
	}
}
