package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

import main.java.types.Story;
import main.java.types.StoryTag;

public class TaggingService extends MongoService {
	private MongoCollection<Document> db;

	public TaggingService(MongoConnection mc) {
		super.setDb(mc.getDb());
		db = super.getCollection("ArticleTag");
	}

	// get mongoCollection
	public MongoCollection<Document> getCol() {
		return db;
	}

	// check if tag exists
	public Boolean checkForTag(String tagName) {
		if (getDocument(tagName) == null) {
			return false;
		}
		return true;
	}
	public void addStories(Set<Document> toAddToDB) {
		db.insertMany(new ArrayList<Document>(toAddToDB));
	}

	// add tag, and link to story
	// or add a link to the story to a prexisting tag if one already exists
	public void addOrUpdateTag(Story s, String tagName) {
		Document d = getDocument(tagName);
		if (d == null) {
			addTag(tagName);
			d= getDocument(tagName);
		}

		if (d != null) {
			@SuppressWarnings("unchecked")
			List<ObjectId> stories = (List<ObjectId>) d.get("articles");
			stories.add(s.get_id());
			d.replace("articles", stories);
			db.replaceOne(eq("name", tagName), d);
		} else {
			System.out.println("not happenin bro");
		}
	}

	// get tag object from Mongo
	public Document getDocument(String tagName) {
		return db.find(eq("name", tagName)).first();
	}

	// add tag object to Mongo
	public void addTag(String tagName) {
		StoryTag tag = new StoryTag(tagName);
		Document d = makeDocument(tag);
		db.insertOne(d);
	}

	// make json from tag object
	public Document makeDocument(StoryTag tag) {
		Document d = new Document();
		d.put("articles", tag.getArticles());
		d.put("name", tag.getName());
		return d;
	}

}