package main.java;

import static spark.Spark.get;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Main {
	
    @SuppressWarnings("resource")
	public static void main(String[] args) {
    	//for running locally, remove this port line
    	//port(Integer.valueOf(System.getenv("PORT")));
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://heroku_s4r2lcpf:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf");
    		MongoClient mongoClient = new MongoClient(mongoClientURI);
    		MongoDatabase db = mongoClient.getDatabase("test");
    		
    		System.out.println("Connect to database successfully");
            return "asds";
		});
    }
}