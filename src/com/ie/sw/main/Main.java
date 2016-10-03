package com.ie.sw.main;

import static com.mongodb.client.model.Filters.*;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import com.google.gson.Gson;
import com.ie.sw.services.*;
import com.ie.sw.types.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
public class Main {
	
	public static void main(String[] args) {
    	//for running locally, remove this port line
@SuppressWarnings("unused")
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		return "null";
		});
	}
}



