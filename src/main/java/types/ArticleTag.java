package main.java.types;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

public class ArticleTag {

	//MongoID
	private ObjectId _id;
	//tag name, i.e Trump, Syria, Barcelona
	private String name;
	//list of articles containing this tag
	private List<ObjectId> articles;
	
	public ArticleTag(String name) {
		super();
		this._id = new ObjectId();
		this.name = name;
		this.articles = new ArrayList<ObjectId>();
	}
	public ArticleTag(Document d)
	{
		this._id = (ObjectId)d.get("_id");
		this.name = (String) d.get("name");
		this.articles = (List<ObjectId>) d.get("articles");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ObjectId> getArticles() {
		return articles;
	}
	public void setArticles(List<ObjectId> articles) {
		this.articles = articles;
	}
	public ObjectId get_id() {
		return _id;
	}
}
