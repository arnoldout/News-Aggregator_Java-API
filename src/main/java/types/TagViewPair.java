package main.java.types;

import org.bson.Document;
import org.bson.types.ObjectId;

//simple object to store tags, an id for mongo, and the amount of times that user has read
//about that tag
public class TagViewPair {

	private ObjectId _id; 
	private String tag;
	private int viewCount;

	public TagViewPair(ObjectId id, String tag, int viewCount) {
		super();
		this._id = new ObjectId();
		this.tag = tag;
		this.viewCount = viewCount;
	}
	public TagViewPair() {
		super();
	}
	public String getTag() {
		return tag;
	}
	public Integer getViewCount() {
		return viewCount;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public void incrementViewCount()
	{
		this.viewCount++;
	}
	public void decrementViewCount()
	{
		this.viewCount--;
	}
	public Document makeDocument()
	{
		Document d = new Document();
		d.put("_id", this._id);
		d.put("tag", this.tag);
		d.put("viewCount", this.viewCount);
		return d;
	}
}