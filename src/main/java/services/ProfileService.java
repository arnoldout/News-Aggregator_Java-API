package main.java.services;

public class ProfileService extends MongoService {
	
	public ProfileService(MongoConnection db) {
		super();
		super.setDb(db.getDb());
	}
	
	
}
