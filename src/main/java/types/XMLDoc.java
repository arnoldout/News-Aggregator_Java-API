package main.java.types;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
//abstract class relating to the basic data that XML-RSS readers need
public abstract class XMLDoc {
	private Queue<Story> newsItems = new ArrayBlockingQueue<Story>(360);

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

	public Queue<Story> getNewsItems() {
		return newsItems;
	}
	public void setNewsItems(Queue<Story> newsItems) {
		this.newsItems = newsItems;
	}

	public boolean add(Story e) {
		return newsItems.add(e);
	}

}
