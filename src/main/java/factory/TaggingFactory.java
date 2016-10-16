package main.java.factory;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import main.java.services.ArticleService;
import main.java.services.MongoConnection;
import main.java.services.TaggingService;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.TagViewPair;

public class TaggingFactory {

	private TaggingService ts;
	private ArticleService as;
	
	public TaggingFactory(MongoConnection mc) {
		super();
		this.ts = new TaggingService(mc);
		this.as = new ArticleService(mc);
	}

	public void generateTags(Story story)
	{
		as.addArticle(story);
		try{
			for(String str : story.getCategories())
			{
				if(!ts.checkForTag(str))
				{
					//create tag, add story to it
					ts.addTag(str);
				}
				//add story to tag
				ts.addStory(story, str);
			}
		}
		catch(NullPointerException e)
		{
			//parsing failed on this article
			//leave it alone, its already added to mongo, it can still be used with other stories
		}
	}

	public JSONArray getPreferredArticles(Profile p)
	{
		JSONArray ja = new JSONArray();
		
		//List<Story> articles = new ArrayList<Story>();
		
		for(TagViewPair tvp : p.getLikes())
		{
			Document d = (Document) ts.getCol().find(eq("name", tvp.getTag()));
			@SuppressWarnings("unchecked")
			List<ObjectId> tagArticles = (List<ObjectId>) d.get("articles");
			for(ObjectId id : tagArticles)
			{
				JSONObject jo = new JSONObject(as.getMongoDocument(id).toJson());
				ja.put(jo);
			}
		}
		return ja;
	}
}
