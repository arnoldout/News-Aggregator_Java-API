package main.java;

import static spark.Spark.*;

import services.MongoConnection;
import services.ProfileService;
public class Main {
	
	public static void main(String[] args) {
    	//for running locally, remove this port line
    	//port(Integer.valueOf(System.getenv("PORT")));
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		MongoConnection mc = new MongoConnection("mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf", "heroku_s4r2lcpf");
    		ProfileService ps = new ProfileService(mc.getDb());
    		return "asds";
		});
    	post("/addProfile", (request, response) -> 
    	{
    		String s = request.body();
    		s = s;
    		return "Hello World: " + request.body();
    	});
    }
}