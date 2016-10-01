package main.java;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.json.JSONException;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;

import services.MongoConnection;
import services.ProfileService;
import types.Profile;
public class Main {
	
	public static void main(String[] args) {
    	//for running locally, remove this port line
    	//port(Integer.valueOf(System.getenv("PORT")));
    	
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
    		//make sure json is a valid Pofile JSON object
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
    			Collection<Object> o = p.values();
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



