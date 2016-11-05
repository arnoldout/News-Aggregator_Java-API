package main.java.factory;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

import main.java.services.ArticleService;
import main.java.services.GsonWrapper;
import main.java.services.MongoConnection;
import main.java.services.ProfileService;
import main.java.services.TaggingService;
import main.java.types.Profile;
import main.java.types.Story;
import main.java.types.TagViewPair;

public class TaggingFactory {

	private TaggingService ts;
	private ArticleService as;
	private ProfileService ps;
	private MongoConnection mc;
	public TaggingFactory(MongoConnection mc) {
		super();
		this.mc = mc;
		this.ts = new TaggingService(mc);
		this.as = new ArticleService(mc);
		this.ps = new ProfileService(mc);
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
		GsonWrapper gw = new GsonWrapper();
		Gson g = gw.getGson();
		MongoCollection<Document> keyPairs = mc.getDb().getCollection("tagPairs");
		Set<String> uniqueUris = new HashSet<String>();
		List<TagViewPair> tvps = new ArrayList<TagViewPair>();
		for(String id : p.getLikes())
		{
			Document tagPair = (Document) keyPairs.find(eq("_id", new ObjectId(id))).first();
			TagViewPair tvp = g.fromJson(tagPair.toJson(), TagViewPair.class);
			tvps.add(tvp);
			Document d = (Document) ts.getCol().find(eq("name", tvp.getTag())).first();
			if(d!=null)
			{
				@SuppressWarnings("unchecked")
				List<ObjectId> tagArticles = (List<ObjectId>) d.get("articles");
				for(ObjectId oid : tagArticles)
				{
					JSONObject jo = new JSONObject(as.getMongoDocument(oid).toJson());
					if(uniqueUris.add((String) jo.get("uri")))
					{
						ja.put(jo);	
					}
				}
			}
		}
		Collections.sort(tvps, new Comparator<TagViewPair>() {
	        @Override
	        public int compare(TagViewPair o1, TagViewPair o2) {
	            return Integer.compare(o2.getViewCount(), o1.getViewCount());
	        }
	    });
		p.clearLikes();
		for(TagViewPair tvp : tvps)
		{
			p.addLike(tvp.get_id());
		}
		ps.updateProfile(p);
		return ja;
	}
}
