package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import main.java.factory.NewsFactory;
import main.java.factory.TaggingFactory;
import main.java.services.ArticleService;
import main.java.services.GsonWrapper;
import main.java.services.MongoConnection;
import main.java.services.ProfileService;
import main.java.types.FrequentlyUsedWords;
import main.java.types.HTMLDoc;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.XMLDoc;

public class Main {

	public static void main(String[] args) {
		// for running locally, remove this port line
		port(Integer.valueOf(System.getenv("PORT")));

		MongoConnection mc = new MongoConnection(
				"mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf", "heroku_s4r2lcpf");
		ProfileService ps = new ProfileService(mc);

		FrequentlyUsedWords fuw = new FrequentlyUsedWords();
		fuw.generateWords();

		Timer mongoGarbageCol = new Timer();
		TimerTask mGC = new TimerTask() {
			@Override
			public void run() {
				GsonWrapper gw = new GsonWrapper();
				Gson g = gw.getGson();
				ArticleService as = new ArticleService(mc);
				MongoCollection<Document> articles = as.getCollection("Article");
				MongoCollection<Document> tags = as.getCollection("ArticleTag");
				FindIterable<Document> docs = articles.find();
				for (Document d : docs) {
					Calendar now = Calendar.getInstance();
					Long l = (Long) d.get("dateTime");
					Date docDate = new Date(l);
					Calendar docTime = Calendar.getInstance();
					docTime.setTime(docDate);
					docTime.add(Calendar.HOUR_OF_DAY, 12);
					if (docTime.before(now)) {
						// remove Article
						Story st = g.fromJson(d.toJson(), Story.class);
						as.removeArticle(st);
					} else {
						// leave article
						System.out.println("af");
					}
				}
			}
		};
		// start in half an hour, run every 12 hours
		mongoGarbageCol.schedule(mGC, 1000 * 60 * 30, 1000 * 60 * 60 * 12);

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Set<String> articles = new HashSet<String>();
				ArticleService as = new ArticleService(mc);
				MongoCollection<Document> col = as.getCollection("Article");
				FindIterable<Document> docs = col.find();
				for (Document d : docs) {
					articles.add(d.getString("uri"));
				}
				Set<Story> storyCol = new HashSet<Story>();
				NewsFactory nf = new NewsFactory();
				nf.getDocs();
				long startTime = System.currentTimeMillis() % 1000;
				ExecutorService executor = Executors.newFixedThreadPool(70);
				TaggingFactory tf = new TaggingFactory(mc);
				for (XMLDoc d : nf.docs) {
					for (Story s : d.getNewsItems()) {
						HTMLDoc doc = new HTMLDoc();
						doc.url = s.getUri();
						if (!(articles.contains(doc.url))) {
							executor.submit(() -> {
								s.setCategories(doc.parseText(fuw.getFreqWords()));
								storyCol.add(s);
								tf.generateTags(s);
							});
						}
					}
				}
				executor.shutdown();
				try {
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Thread Broken");
				}
				// TaggingFactory.generateTags(st, mc);
				long endTime = System.currentTimeMillis() % 1000;
				System.out.println(endTime - startTime);
				// all stories now categorized each hour with top three tags
				System.out.println("done");
			}
		};
		timer.schedule(task, 01, 1000 * 60 * 60);

		// basic help response to a blank call to the webpage
		get("/", (request, response) -> {
			return "null";
		});
		get("/getArticles/:id", (request, response) -> {
			String id = request.params(":id");
			TaggingFactory tf = new TaggingFactory(mc);
			return tf.getPreferredArticles(ps.getProfile(new ObjectId(id)));
			
		});
		get("/addLike/:id/:like", (request, response) -> {
			String id = request.params(":id");
			String like = request.params(":like");
			ps.incrementTag(like, new ObjectId(id));
			return "";
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

		post("/changePassword", (request, response) -> {
			request.body();
			return "";
		});
	}
}
