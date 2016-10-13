package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.java.services.MongoConnection;
import main.java.services.ProfileService;
import main.java.types.FrequentlyUsedWords;
import main.java.types.HTMLDoc;
import main.java.types.NewsFactory;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.XMLDoc;

public class Main {

	public static void main(String[] args) {
		// for running locally, remove this port line
		port(Integer.valueOf(System.getenv("PORT")));

		FrequentlyUsedWords fuw = new FrequentlyUsedWords();
		fuw.generateWords();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				NewsFactory nf = new NewsFactory();
				nf.getDocs();
				long startTime = System.currentTimeMillis() % 1000;
				ExecutorService executor = Executors.newFixedThreadPool(70);
				for (XMLDoc d : nf.docs) {
					for (Story s : d.getNewsItems()) {
						HTMLDoc doc = new HTMLDoc();		
						doc.url = s.getUri();
						executor.submit(() -> {
							s.setCategories(doc.parseText(fuw.getFreqWords()));
						});
					}	
				}
				executor.shutdown();
				try {
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Thread Broken");
				}
				long endTime = System.currentTimeMillis() % 1000;
				System.out.println(endTime-startTime);
				//all stories now categorized each hour with top three tags
				System.out.println("done");
			}
		};
		timer.schedule(task, 0l, 1000 * 60 * 60);

		MongoConnection mc = new MongoConnection(
				"mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf", "heroku_s4r2lcpf");
		ProfileService ps = new ProfileService(mc.getDb());

		// basic help response to a blank call to the webpage
		get("/", (request, response) -> {
			return "null";
		});

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
		post("/login", (request, response) -> {
			Gson g = new Gson();

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
				// invalid account
				return false;
			}

			return "";
		});
		post("/addProfile", (request, response) -> {
			Gson g = new Gson();

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
				String s = (String) p.get("username");
				if (s.equals(dbo.get("username"))) {
					// name taken
					return false;
				}
			}
			col.insertOne(dbo);
			return dbo.get("_id").toString();
		});

		post("/changePassword", (request, response) -> {
			request.body();
			return "";
		});
	}
}
