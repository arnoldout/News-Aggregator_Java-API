package main.java.types;

import org.bson.types.ObjectId;

public class TagViewPair {

	private String tag;
	private int viewCount;

	public TagViewPair(String tag, int viewCount) {
		super();
		this.tag = tag;
		this.viewCount = viewCount;
	}
	public String getTag() {
		return tag;
	}
	public Integer getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public void incrementViewCount()
	{
		this.viewCount++;
	}
}