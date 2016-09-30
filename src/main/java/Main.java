package main.java;

import static spark.Spark.get;
import static spark.SparkBase.port;

import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
public class Main {
	
    @SuppressWarnings("resource")
	public static void main(String[] args) {
    	//for running locally, remove this port line
    	//port(Integer.valueOf(System.getenv("PORT")));
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf"));
    		
    		return "asds";
		});	
    }
}