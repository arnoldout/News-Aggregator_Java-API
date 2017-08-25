package main.java.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bson.types.ObjectId;
//Story object is a news article
public class Story {
	
	//Mongo ID
	private ObjectId _id;
	//title of article
	private String title;
	//short description of story
    private String description;
    //uri linking to article
    private String uri;
    //list of tags relating to story
    private List<String> categories = new ArrayList<String>();
    //date story created in this program
    private long ldt;
    //associated image to article
    private String imgUri;
    public Story(NeverNullString uri, NeverNullString title, NeverNullString desc, NeverNullString imgUri) {
		super();
		this.uri = uri.getString();
		this.title = title.getString();
		this.description = desc.getString();
		this.imgUri = imgUri.getString();
		this._id = new ObjectId();	
		//setting time of creation as the current datetime in long form
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
