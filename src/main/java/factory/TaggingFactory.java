package main.java.factory;

import main.java.services.ArticleService;
import main.java.services.MongoConnection;
import main.java.services.TaggingService;
import main.java.types.Story;

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
}
