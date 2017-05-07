package main.java;

import static com.mongodb.client.model.Filters.eq;
import static spark.Spark.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
import main.java.services.TaggingService;
import main.java.types.FrequentlyUsedWords;
import main.java.types.HTMLDoc;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.XMLDoc;

public class Main {

	public static void main(String[] args) {
		// for running locally, remove this port line
		//port(Integer.valueOf(System.getenv("PORT")));

		// mongo connection string
		MongoConnection mc = new MongoConnection(
				"mongodb://arnoldout111:mongopassword1@ds035026.mlab.com:35026/heroku_s4r2lcpf", "heroku_s4r2lcpf");
		ProfileService ps = new ProfileService(mc);

		// used as blacklist when crawling pages
		FrequentlyUsedWords fuw = new FrequentlyUsedWords();
		fuw.generateWords();

		Timer mongoGarbageCol = new Timer();
		TimerTask mGC = new TimerTask() {
			@Override
			public void run() {
				/*
				 * clean up mongo every hour, half an hour after init running
				 * main
				 * 
				 * remove any news articles that have been stored in mongo for
				 * 12 hours
				 */
				GsonWrapper gw = new GsonWrapper();
				Gson g = gw.getGson();
				ArticleService as = new ArticleService(mc);
				MongoCollection<Document> articles = as.getCollection("Article");
				FindIterable<Document> docs = articles.find();
				for (Document d : docs) {
					Calendar now = Calendar.getInstance();
					Long l = (Long) d.get("dateTime");
					Date docDate = new Date(l);
					Calendar docTime = Calendar.getInstance();
					docTime.setTime(docDate);
					docTime.add(Calendar.HOUR_OF_DAY, 12);
					// check if date on article is 12 hours before present time
					if (docTime.before(now)) {
						// remove Article
						Story st = g.fromJson(d.toJson(), Story.class);
						as.removeArticle(st);
					}
				}
			}
		};
		// start in half an hour, run every 1 hour
		mongoGarbageCol.schedule(mGC, 1000 * 60 * 30, 1000 * 60 * 60);

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				/*
				 * Every hour, check rss feeds for new content
				 */
				Set<String> articles = new HashSet<String>();
				ArticleService as = new ArticleService(mc);
				MongoCollection<Document> col = as.getCollection("Article");
				FindIterable<Document> docs = col.find();
				for (Document d : docs) {
					// add uris to new articles
					articles.add(d.getString("uri"));
				}
				Set<Story> storyCol = new HashSet<Story>();
				NewsFactory nf = new NewsFactory();
				nf.getDocs();
				ExecutorService executor = Executors.newFixedThreadPool(70);
				TaggingFactory tf = new TaggingFactory(mc);
				// parse article bodies for keywords and tags
				// check keywords against blacklist of frequently occuring
				// english words
				TaggingService ts = new TaggingService(mc);
				Map<String, Queue<Story>> tags = new ConcurrentHashMap<String, Queue<Story>>();
				FindIterable<Document> b = ts.getCollection("ArticleTag").find();
				for (Document doc2 : b) {
					tags.put((String) doc2.get("name"), new ArrayBlockingQueue<Story>(360));
				}
				System.out.println("Current Tags size:" + tags.size());
				for (XMLDoc d : nf.getParsedDocs()) {
					for (Story s : d.getNewsItems()) {
						HTMLDoc doc = new HTMLDoc();
						doc.url = s.getUri();
						if (!(articles.contains(doc.url))) {
							s.setCategories(doc.parseText(fuw.getFreqWords()));
							storyCol.add(s);
							tf.generateTags(s, tags);
						}
					}
				}
				System.out.println("Current Tags size:" + tags.size());

				for (Entry<String, Queue<Story>> entry : tags.entrySet()) {
					// get or add and get tag object
					String key = entry.getKey();
					Queue<Story> value = entry.getValue();
					executor.submit(() -> {
						while (!value.isEmpty()) {
							ts.addStory(value.poll(), key);
						}
					});
				}
				System.out.println("DONE");
				executor.shutdown();
				try {
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted");
				}
				// all stories now categorized each hour with top three tags
			}
		};
		// run task every hour
		timer.schedule(task, 01, 1000 * 60 * 60);

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
			ArticleService as = new ArticleService(mc);
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
		 * unimplemented post method post("/changePassword", (request, response)
		 * -> { request.body(); return ""; });
		 */
	}
}
