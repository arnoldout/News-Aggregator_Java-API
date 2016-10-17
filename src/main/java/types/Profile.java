package main.java.types;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Profile {
	private ObjectId _id;
	private String username;
	private String password;
	private List<String> likes = new ArrayList<String>();

	public Profile() {
		super();
	}
	public void addLike(ObjectId oid)
	{
		this.likes.add(oid.toString());
	}
	public List<String> getLikes()
	{
		return this.likes;
	}
	public ObjectId getId()
	{
		return this._id;
	}
	public void set_Id(ObjectId id)
	{
		this._id = id;
	}
	public Document makeDocument()
	{
		Document d = new Document();
		//if adding more fields, this needs to also be edited
		d.append("_id", _id);
		d.append("username", username);
		d.append("password", password);
		d.append("likes", likes);
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
}
