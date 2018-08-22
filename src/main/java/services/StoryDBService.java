package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import main.java.types.Story;

public class StoryDBService extends MongoService{
private MongoCollection<Document> col;
	
	public StoryDBService(MongoConnection mc)
	{
		super.setDb(mc.getDb());
		col = super.getCollection("Article");
	}
	
	public Boolean ifTagExists(String tagName)
	{
		if(getMongoDocument(tagName)==null)
		{
			return false;
		}
		return true;
	}
	public Document getMongoDocument(String tagName)
	{
		//get article from mongo from tag name
		return col.find(eq("name", tagName)).first();
	}
	public Document getMongoDocument(ObjectId id)
	{
		//get article with id
		return col.find(eq("_id", id)).first();
	}
	public void addArticle(Story story)
	{
		//insert article to mongo
		Document d = makeDocument(story);
		col.insertOne(d);
	}
	public Document makeDocument(Story story)
	{
		//convert story object to JSON
		Document d = new Document();
		d.put("_id", story.get_id());
		d.put("categories", story.getCategories());
		d.put("description", story.getDescription());
		d.put("title", story.getTitle());
		d.put("uri", story.getUri());
		d.put("dateTime", story.getLdt());
		d.put("imgUri", story.getImgUri());
		return d;
	}
	public JSONObject getArticles()
	{
		//get article tags from mongo
		MongoCollection<Document> tagsCol = super.getCollection("ArticleTag");
		FindIterable<Document> allTags = tagsCol.find();
		List<String> tags = new ArrayList<String>();
		JSONObject jo = new JSONObject();
 		for(Document d : allTags)
		{
 			//just get tag names
			tags.add(d.getString("name"));
		}
		jo.append("articles", tags);
		return jo;
	}
	public FindIterable<Document> getAllArticles()
	{
		MongoCollection<Document> tagsCol = super.getCollection("ArticleTag");
		return tagsCol.find();
	}
	public void removeTag(List<ObjectId> id)
	{
		MongoCollection<Document> tagsCol = super.getCollection("ArticleTag");
		//tagsCol.deleteOne(eq("_id", id));
		tagsCol.deleteMany(Filters.in("_id", id));
	}
	
//	//get tags that have no assigned articles
//	public List<ObjectId> getEmptyArticles()
//	{
//		FindIterable<Document> allTags = getAllArticles();
//		List<ObjectId> emptyTags = new ArrayList<ObjectId>();
//		for(Document d : allTags)
//		{
//			ArrayList<?> articles = (ArrayList<?>) d.get("articles");
//			if(articles.isEmpty())
//			{
//				emptyTags.add((ObjectId) d.get("_id"));
//			}
//		}
//		return emptyTags;
//	}
	//remove article from mongo
	public void removeArticle(Story story)
	{
		MongoCollection<Document> tagsCol = super.getCollection("ArticleTag");
		try{
			for(String s:story.getCategories())
			{
				Document d = (Document) tagsCol.find(eq("name", s)).first();
				@SuppressWarnings("unchecked")
				List<ObjectId> li = (List<ObjectId>) d.get("articles");
				li.remove(story.get_id());
				d.replace("articles", li);
				tagsCol.replaceOne(eq("name", s), d);
			}
			col.deleteOne(eq("_id", story.get_id()));
		}
		catch(NullPointerException e)
		{
			//no assigned categories
		}
	}
}
