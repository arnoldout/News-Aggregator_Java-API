package services;

import com.mongodb.client.MongoDatabase;

public class ProfileService {
	MongoDatabase db;

	public MongoDatabase getDb() {
		return db;
	}

	public ProfileService(MongoDatabase db) {
		super();
		this.db = db;
	}
}
