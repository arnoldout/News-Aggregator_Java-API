package main.java.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.java.types.Story;

public class GarbageCollectionService {

	public void startGC(MongoConnection mc) {

		// start in half an hour, run every 1 hour
		Timer mongoGarbageCol = new Timer();
		mongoGarbageCol.schedule(storyCleanerUpper(mc), 1000 * 60 * 30, 1000 * 60 * 60);
	}

	private static TimerTask storyCleanerUpper(MongoConnection mc) {
		TimerTask mGC = new TimerTask() {
			@Override
			public void run() {
				/*
				 * clean up mongo every hour, half an hour after init running main
				 * 
				 * remove any news articles that have been stored in mongo for 12 hours
				 */
				GsonWrapper gw = new GsonWrapper();
				Gson g = gw.getGson();
				StoryDBService as = new StoryDBService(mc);
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
		return mGC;
	}

}
