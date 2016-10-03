package main.ie.types;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Profile {
	private ObjectId id;
	private String username;
	private String password;

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
	public Document makeDocument()
	{
		Document d = new Document();
		//if adding more fields, this needs to also be edited
		d.append("username", username);
		d.append("password", password);
		d.append("_id", id);
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
