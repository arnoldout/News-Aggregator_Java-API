package main.java.factory;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import main.java.services.StoryDBService;
import main.java.services.GsonWrapper;
import main.java.services.MongoConnection;
import main.java.services.ProfileService;
import main.java.services.TaggingService;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.TagViewPair;

public class TaggingFactory {

	static class PQsort implements Comparator<TagViewPair> {

		public int compare(TagViewPair one, TagViewPair two) {
			return two.getViewCount() - one.getViewCount();
		}
	}

	private TaggingService ts;
	private StoryDBService as;
	private ProfileService ps;
	private MongoConnection mc;

	public TaggingFactory(MongoConnection mc) {
		super();
		this.mc = mc;
		this.ts = new TaggingService(mc);
		this.as = new StoryDBService(mc);
		this.ps = new ProfileService(mc);
	}

	public void generateTags(Story story, Map<String, Queue<Story>> tags) {
		try {
			for (String str : story.getCategories()) {
				if (!tags.containsKey(str)) {
					tags.put(str, new ArrayBlockingQueue<Story>(90));
				}
				tags.get(str).offer(story);
			}
		} catch (NullPointerException e) {
			// parsing failed on this article
			// leave it alone, its already added to mongo, it can still be used
			// with other stories
		}
	}

	public JSONArray getPreferredArticles(Profile p) {
		// get all articles with same tags as user's likes
		JSONArray ja = new JSONArray();
		GsonWrapper gw = new GsonWrapper();
		Gson g = gw.getGson();
		MongoCollection<Document> keyPairs = mc.getDb().getCollection("tagPairs");
		Map<String, Integer> uniqueUris = new HashMap<String, Integer>();
		PQsort pqs = new PQsort();
		Queue<TagViewPair> tvps = new PriorityQueue<TagViewPair>(1000, pqs);
		// looping over user's likes
		for (String id : p.getLikes()) {
			// getting tag pair from mongo, deserializing down to object
			Document tagPair = (Document) keyPairs.find(eq("_id", new ObjectId(id))).first();
			TagViewPair tvp = g.fromJson(tagPair.toJson(), TagViewPair.class);

			// add to tags priority queue
			tvps.add(tvp);

			// get tag object by name found in tvp, will hold all available
			// articles
			Document d = (Document) ts.getCol().find(eq("name", tvp.getTag())).first();
			// get current time, store as constant for checking so all times
			// checks are constant
			Calendar c = Calendar.getInstance();
			long currentTime = c.getTime().getTime();
			if (d != null) {
				@SuppressWarnings("unchecked")
				// get all the articles
				List<ObjectId> tagArticles = (List<ObjectId>) d.get("articles");
				// loop over article ids
				for (ObjectId oid : tagArticles) {
					// make sure user hasn't read story before
					if (!p.getHistory().contains(oid.toString())) {
						// get article content, store in map if unique
						JSONObject currentStory = new JSONObject(as.getMongoDocument(oid).toJson());
						// check if story has been added, increment the tags or
						// set the tags view count
						if (!uniqueUris.containsKey(currentStory.toString())) {
							/*
							 * use site for a few days, see how it goes
							 * more than likely that 1000000 will need to be changed,
							 * at this point its just a balancing act, how heavily I want to weight down old stories 
							 * and promote new ones over tags, see how it goes
							 */
							long uploadTime = Long.parseLong((String) ((JSONObject) currentStory.get("dateTime")).get("$numberLong"));
							int timeDiff = (int) ((currentTime-uploadTime)/100000);
							uniqueUris.put(currentStory.toString(), (tvp.getViewCount()-timeDiff));

						} else {
							uniqueUris.put(currentStory.toString(),
									uniqueUris.get(currentStory.toString()) + tvp.getViewCount());
						}
					}
				}
			}
		}
		// sort map by value, put json array
		List<String> keys = new ArrayList<String>(uniqueUris.keySet());
		List<Integer> values = new ArrayList<Integer>(uniqueUris.values());
		Collections.sort(keys, Comparator.comparing(item -> values.indexOf(item)));
		for (String j : keys) {
			ja.put(new JSONObject(j));
		}
		p.clearLikes();
		while (!tvps.isEmpty()) {
			p.addLike(tvps.poll().get_id());
		}
		ps.updateProfile(p);
		return ja;

	}

}
