package main.java.services;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

import main.java.types.ArticleTag;
import main.java.types.Story;

public class TaggingService extends MongoService {
	private MongoCollection<Document> col;
	
	public TaggingService(MongoConnection mc)
	{
		super.setDb(mc.getDb());
		col = super.getCollection("ArticleTag");
	} 	
	public MongoCollection<Document> getCol(){
		return col;
	}
	public Boolean checkForTag(String tagName)
	{
		if(getDocument(tagName)==null)
		{
			return false;
		}
		return true;
	}
	public void addStory(Story s, String tagName)
	{
		Document d = getDocument(tagName);
		if(d!=null){
			@SuppressWarnings("unchecked")
			List<ObjectId> stories = (List<ObjectId>) d.get("articles");
			stories.add(s.get_id());
			d.replace("articles", stories);
			col.replaceOne(eq("name", tagName), d);
		}
		else{
			System.out.println("not happenin bro");
		}
	}
	public Document getDocument(String tagName)
	{
		return col.find(eq("name", tagName)).first();
	}
	public void addTag(String tagName)
	{
		ArticleTag tag = new ArticleTag(tagName);
		Document d = makeDocument(tag);
		col.insertOne(d);
	}
	public Document makeDocument(ArticleTag tag)
	{
		Document d = new Document();
		d.put("articles", tag.getArticles());
		d.put("name", tag.getName());
		return d;
	}

}