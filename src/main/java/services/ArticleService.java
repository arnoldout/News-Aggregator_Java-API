package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

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
		if(getDocument(tagName)==null)
		{
			return false;
		}
		return true;
	}
	public Document getDocument(String tagName)
	{
		return col.find(eq("name", tagName)).first();
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
		return d;
	}
}
