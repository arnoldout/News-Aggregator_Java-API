package main.java.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bson.types.ObjectId;

public class Story {
	private ObjectId _id;
	private String title;
    private String description;
    private String uri;
    private List<String> categories = new ArrayList<String>();
    private long ldt;
    private String imgUri;
    public Story(String uri) {
		super();
		this.uri = uri;
		this._id = new ObjectId();
		Calendar c = Calendar.getInstance();
		this.setLdt(c.getTime().getTime());
	} 
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public List<String> getCategories() {
		return categories;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public long getLdt() {
		return ldt;
	}
	public void setLdt(long ldt) {
		this.ldt = ldt;
	}
	public String getImgUri() {
		return imgUri;
	}
	public void setImgUri(String imgUri) {
		this.imgUri = imgUri;
	}

}
