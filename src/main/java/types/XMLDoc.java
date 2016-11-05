package main.java.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public abstract class XMLDoc {
	private List<Story> newsItems = new ArrayList<Story>();

	public abstract void parseXml();

	public Document getXML(String url) {
		try{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public List<Story> getNewsItems() {
		return newsItems;
	}
	public void setNewsItems(List<Story> newsItems) {
		this.newsItems = newsItems;
	}

	public boolean add(Story e) {
		return newsItems.add(e);
	}

}
