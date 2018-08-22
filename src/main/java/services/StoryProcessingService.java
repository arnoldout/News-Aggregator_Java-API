package main.java.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.java.factory.NewsFactory;
import main.java.factory.TaggingFactory;
import main.java.types.FrequentlyUsedWords;
import main.java.types.HTMLDoc;
import main.java.types.Story;
import main.java.types.XMLDoc;
import static main.java.constants.HerokuConstants.*;

public class StoryProcessingService {

	public void startProcessor(MongoConnection mc, FrequentlyUsedWords fuw) {
		Timer timer = new Timer();

		// run task every hour
		timer.schedule(processNewStories(mc, fuw), 01, 1000 * 60 * 60);

	}

	private static TimerTask processNewStories(MongoConnection mc, FrequentlyUsedWords fuw) {

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				//keep GUI alive
				keepAlive();
				
				//init various services needeed
				Set<String> foundInDB = new HashSet<String>();
				StoryDBService articleDBService = new StoryDBService(mc);
				Set<Document> toAddToDB = new HashSet<Document>();
				NewsFactory newsFactory = new NewsFactory();
				ExecutorService executor = Executors.newFixedThreadPool(70);
				TaggingService taggingService = new TaggingService(mc);
				Map<String, Queue<Story>> generatedTags = new ConcurrentHashMap<String, Queue<Story>>();
				
				//go to db, populate local lists with findings
				FindIterable<Document> foundTags = taggingService.getCollection("ArticleTag").find();
				FindIterable<Document> DBArticleCollection = articleDBService.getCollection("Article").find();
				initDBCollections(foundInDB, generatedTags, foundTags, DBArticleCollection);
				
				//go to rss feeds, get all stories available
				newsFactory.generateDocs(fuw, generatedTags, mc);
				
				for (Story tmpStory : newsFactory.getStories()) {				
					if (!(foundInDB.contains(tmpStory.getUri()))) {
						toAddToDB.add(new StoryDBService(mc).makeDocument(tmpStory));
					}
				}
				for (Entry<String, Queue<Story>> entry : generatedTags.entrySet()) {
					// get or add and get tag object
					String key = entry.getKey();
					Queue<Story> value = entry.getValue();
					executor.submit(() -> {
						while (!value.isEmpty()) {
							taggingService.addOrUpdateTag(value.poll(), key);
						}
					});
				}
				executor.shutdown();
				try {
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Thread Interrupted");
				}
				// all stories now categorized each hour with top three tags
				System.out.println("DONE");
		}};
		return task;
	}

	private static void initDBCollections(Set<String> foundInDB, Map<String, Queue<Story>> generatedTags,
			FindIterable<Document> foundTags, FindIterable<Document> DBSotries) {
		// make a list of all stories currently in the db by URL
		for (Document mongoStoryDocument : DBSotries) {
			// add uris to new stories
			foundInDB.add(mongoStoryDocument.getString("uri"));
		}
		for (Document mongoTagDocument : foundTags) {
			generatedTags.put((String) mongoTagDocument.get("name"), new ArrayBlockingQueue<Story>(360));
		}
	}

	/*
	 * Send a ping to Heroku GUI to ensure it's awake
	 */
	private static void keepAlive() {
		try {
			URL obj = new URL(FEED_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + FEED_URL);
			System.out.println("Response Code : " + responseCode);
		} catch (IOException exception) {
			System.out.println("No Ping");
		}
	}

}
