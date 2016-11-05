package main.java.types;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class ArticleTag {

	private ObjectId _id;
	private String name;
	private List<ObjectId> articles;
	
	public ArticleTag(String name) {
		super();
		this._id = new ObjectId();
		this.name = name;
		this.articles = new ArrayList<ObjectId>();
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
