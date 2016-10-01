package com.ie.sw.main;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

import org.bson.Document;
import org.json.JSONException;

import com.google.gson.Gson;
import com.ie.sw.services.*;
import com.ie.sw.types.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
public class Main {
	
	public static void main(String[] args) {
    	//for running locally, remove this port line
    	port(Integer.valueOf(System.getenv("PORT")));
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		return "asds";
		});
    	post("/addProfile", (request, response) -> 
    	{
    		Gson g = new Gson();
    		
    		MongoConnection mc = new MongoConnection("mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf", "heroku_s4r2lcpf");
    		ProfileService ps = new ProfileService(mc.getDb());
    		MongoCollection<Document> col = ps.getCollection("profile");
    		//make sure JSON is a valid Profile JSON object
    		Document dbo = null;
    		try{
    			Profile p = g.fromJson(request.body(), Profile.class);
    			dbo = p.makeDocument();
    		}
    		catch(JSONException e)
    		{
    			response.status(406);
    			return response;
    		}
    		FindIterable<Document>docs = col.find();
    		for(Document p : docs)
    		{
    			String s = (String) p.get("username");
    			if(s.equals(dbo.get("username")))
    			{
    				//name taken
    				return false;
    			}
    		}
    		col.insertOne(dbo);
    		return dbo.get("id").toString();
    	});
    }
}



