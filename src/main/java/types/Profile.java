package main.java.types;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;

public class Profile {
	private ObjectId id;
	private String username;
	private String password;
	private List<TagViewPair> likes = new ArrayList<TagViewPair>();

	public Profile(String username, String password) {
		super();
		id = new ObjectId();
		this.username = username;
		this.password = password;
	}
	public Profile() {
		super();
		id = new ObjectId();
	}
	public ObjectId getId()
	{
		return this.id;
	}
	public List<TagViewPair> getLikes()
	{
		return likes;
	}
	public Document makeDocument()
	{
		Document d = new Document();
		//if adding more fields, this needs to also be edited
		d.append("username", username);
		d.append("password", password);
		d.append("_id", id);
		BasicDBList list = new BasicDBList();
		for(TagViewPair tvp : likes)
		{			
			list.put(tvp.getViewCount(), tvp.getTag());
		}
		d.append("likes", list);
		return d;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void incrementTag(String tagName)
	{
		for(TagViewPair tvp : likes)
		{
			if(tvp.getTag().equals(tagName)){
				tvp.incrementViewCount();
				return;
			}
		}
		likes.add(new TagViewPair(tagName, 1));
	}
}
