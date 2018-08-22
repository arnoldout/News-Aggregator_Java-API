package main.java;

import static com.mongodb.client.model.Filters.eq;
import static main.java.constants.HerokuConstants.MONGO_CLIENT;
import static main.java.constants.HerokuConstants.MONGO_DB;
import static spark.Spark.get;
import static spark.Spark.post;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.java.factory.TaggingFactory;
import main.java.services.GarbageCollectionService;
import main.java.services.GsonWrapper;
import main.java.services.MongoConnection;
import main.java.services.ProfileService;
import main.java.services.StoryDBService;
import main.java.services.StoryProcessingService;
import main.java.types.FrequentlyUsedWords;
import main.java.types.Profile;
import spark.SparkBase;

public class Main {

	public static void main(String[] args) {
		// for running locally, remove this port line
		if(System.getenv("PORT")!=null) {
			SparkBase.port(Integer.valueOf(System.getenv("PORT")));	
		}
		
		// mongo connection string
		MongoConnection mc = new MongoConnection(MONGO_CLIENT, MONGO_DB);
		ProfileService ps = new ProfileService(mc);

		// used as blacklist when crawling pages
		FrequentlyUsedWords fuw = new FrequentlyUsedWords();
		fuw.generateWords();

		// start garbage collection service
		new GarbageCollectionService().startGC(mc);

		new StoryProcessingService().startProcessor(mc, fuw);

		// basic help response to a blank call to the webpage
		get("/", (request, response) -> {
			return "null";
		});
		// get all news stories containing tags in common with the user
		get("/getArticles/:id", (request, response) -> {
			// user's id
			String id = request.params(":id");
			TaggingFactory tf = new TaggingFactory(mc);
			return tf.getPreferredArticles(ps.getProfile(new ObjectId(id)));

		});
		// add like to account with user's id
		get("/addLike/:id/:like", (request, response) -> {
			String id = request.params(":id");
			String like = request.params(":like");
			ps.incrementTag(like, new ObjectId(id));
			return "";
		});
		// add like to account with user's id
		get("/remLike/:id/:like", (request, response) -> {
			String id = request.params(":id");
			String like = request.params(":like");
			ps.decrementTag(like, new ObjectId(id));
			return "";
		});
		// get all available likes in mongo
		get("/allLikes", (request, response) -> {
			StoryDBService as = new StoryDBService(mc);
			return as.getArticles();
		});

		// get all available likes in mongo
		get("/readArticle/:id/:url", (request, response) -> {
			Profile p = ps.getProfile(new ObjectId(request.params(":id")));
			p.getHistory().add(request.params(":url"));
			ps.updateProfile(p);
			return "true";
		});
		// get full user profile from user's id
		get("/getProfile/:profileId", (request, response) -> {
			String id = request.params(":profileId");
			MongoCollection<Document> col = ps.getCollection("profile");
			try {
				Document d = col.find(eq("_id", new ObjectId(id))).first();
				if (d != null) {
					// client shouldn't know the password
					d.remove("password");
					return d;
				}
				return "false";
			} catch (IllegalArgumentException e) {
				// invalid Objectid
				return "false";
			}

		});
		// login a user, with a sign in credential in the post body
		// return a user id from a username and password
		post("/login", (request, response) -> {
			GsonWrapper gw = new GsonWrapper();
			Gson g = gw.getGson();

			MongoCollection<Document> col = ps.getCollection("profile");
			// make sure JSON is a valid Profile JSON object
			Document dbo = null;
			try {
				Profile p = g.fromJson(request.body(), Profile.class);
				dbo = p.makeDocument();
			} catch (JSONException e) {
				response.status(406);
				return response;
			}
			FindIterable<Document> docs = col.find();
			for (Document p : docs) {
				String nme = (String) p.get("username");
				String pwd = (String) p.get("password");
				if (nme.equals(dbo.get("username")) && pwd.equals(dbo.get("password"))) {
					// valid user account
					return p.get("_id").toString();
				}
			}
			return "false";
		});

		// register new user if username is unique
		post("/addProfile", (request, response) -> {
			GsonWrapper gw = new GsonWrapper();
			Gson g = gw.getGson();

			MongoCollection<Document> col = ps.getCollection("profile");
			// make sure JSON is a valid Profile JSON object
			Document dbo = null;
			try {
				Profile p = g.fromJson(request.body(), Profile.class);
				p.set_Id(new ObjectId());
				dbo = p.makeDocument();
			} catch (JSONException e) {
				response.status(406);
				return response;
			}
			FindIterable<Document> docs = col.find();
			for (Document p : docs) {
				String s = (String) p.get("username");
				if (s.equals(dbo.get("username"))) {
					// name taken
					return false;
				}
			}
			col.insertOne(dbo);
			return dbo.get("_id").toString();
		});

		/*
		 * unimplemented post method post("/changePassword", (request, response) -> {
		 * request.body(); return ""; });
		 */
	}

}
