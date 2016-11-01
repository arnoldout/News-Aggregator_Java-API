package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import main.java.types.Profile;
import main.java.types.Story;

public class ArticleService extends MongoService{
private MongoCollection<Document> col;
	
	public ArticleService(MongoConnection mc)
	{
		super.setDb(mc.getDb());
		col = super.getCollection("Article");
	}
	public Boolean checkFor(String tagName)
	{
		if(getMongoDocument(tagName)==null)
		{
			return false;
		}
		return true;
	}
	public Document getMongoDocument(String tagName)
	{
		return col.find(eq("name", tagName)).first();
	}
	public Document getMongoDocument(ObjectId id)
	{
		return col.find(eq("_id", id)).first();
	}
	public void addArticle(Story story)
	{
		Document d = makeDocument(story);
		col.insertOne(d);
	}
	public Document makeDocument(Story story)
	{
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
	public Document getArticles()
	{
		MongoCollection<Document> tagsCol = super.getCollection("ArticleTag");
		FindIterable<Document> allTags = tagsCol.find();
		List<String> tags = new ArrayList<String>();
 		for(Document d : allTags)
		{
			tags.add(d.getString("name"));
		}
		Document d = new Document();
		d.append("articles", tags);
		return d;
	}
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
